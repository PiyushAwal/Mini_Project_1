package com.qrfood.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.qrfood.model.RestaurantTable;
import com.qrfood.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * QRCodeController
 *
 * Generates unique QR code images per table using ZXing library.
 * Base URL: /api/qr
 *
 * Uniqueness is guaranteed because each table has a UUID token stored
 * in PostgreSQL. The QR encodes:
 *   http://localhost:8080/customer.html?table=3&token=<uuid>
 * So Table 1, Table 2, Table 3 all produce completely different QR images.
 *
 * Endpoints:
 *   GET  /api/qr/table/{n}         -> Generate/return QR PNG image
 *   GET  /api/qr/tables            -> List all tables
 *   POST /api/qr/setup-tables      -> Create tables in DB
 *   POST /api/qr/regenerate/{n}    -> Regenerate QR for a table
 */
@RestController
@RequestMapping("/api/qr")
@CrossOrigin(origins = "*")
public class QRCodeController {

    @Autowired
    private TableRepository tableRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    // --------------------------------------------------
    // GET /api/qr/table/{tableNumber}
    // Returns a PNG QR code image for the given table.
    // The QR encodes: /customer.html?table=N&token=<uuid>
    // --------------------------------------------------
    @GetMapping(value = "/table/{tableNumber}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCode(@PathVariable int tableNumber)
            throws WriterException, IOException {

        // Fetch or create table record
        RestaurantTable table = tableRepository.findByTableNumber(tableNumber)
                .orElseGet(() -> {
                    RestaurantTable t = new RestaurantTable(tableNumber);
                    return tableRepository.save(t);
                });

        // Assign a permanent unique token if not yet set
        if (table.getQrToken() == null || table.getQrToken().isBlank()) {
            table.setQrToken(UUID.randomUUID().toString());
            table = tableRepository.save(table);
        }

        // Build the unique URL to encode into the QR image
        String menuUrl = baseUrl
                + "/customer.html?table=" + tableNumber
                + "&token=" + table.getQrToken();

        table.setQrCodeUrl(menuUrl);
        tableRepository.save(table);

        // Generate QR image using ZXing
        QRCodeWriter    writer    = new QRCodeWriter();
        BitMatrix       bitMatrix = writer.encode(menuUrl, BarcodeFormat.QR_CODE, 350, 350);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(out.toByteArray());
    }

    // --------------------------------------------------
    // GET /api/qr/tables
    // List all tables with their token and QR URL
    // --------------------------------------------------
    @GetMapping("/tables")
    public ResponseEntity<List<RestaurantTable>> getAllTables() {
        return ResponseEntity.ok(tableRepository.findAll());
    }

    // --------------------------------------------------
    // POST /api/qr/setup-tables
    // Creates tables in the database (run once at setup)
    // Body: { "count": 10 }
    // --------------------------------------------------
    @PostMapping("/setup-tables")
    public ResponseEntity<List<RestaurantTable>> setupTables(
            @RequestBody Map<String, Integer> body) {

        int count = body.getOrDefault("count", 10);
        for (int i = 1; i <= count; i++) {
            int tableNum = i;
            if (tableRepository.findByTableNumber(tableNum).isEmpty()) {
                tableRepository.save(new RestaurantTable(tableNum));
            }
        }
        return ResponseEntity.ok(tableRepository.findAll());
    }

    // --------------------------------------------------
    // POST /api/qr/regenerate/{tableNumber}
    // Creates a new UUID token for a table (invalidates old QR).
    // Use when a printed QR sticker is lost or damaged.
    // --------------------------------------------------
    @PostMapping("/regenerate/{tableNumber}")
    public ResponseEntity<Map<String, Object>> regenerateQR(
            @PathVariable int tableNumber) {

        RestaurantTable table = tableRepository.findByTableNumber(tableNumber)
                .orElseThrow(() -> new RuntimeException("Table not found: " + tableNumber));

        String newToken = UUID.randomUUID().toString();
        table.setQrToken(newToken);

        String newUrl = baseUrl
                + "/customer.html?table=" + tableNumber
                + "&token=" + newToken;
        table.setQrCodeUrl(newUrl);
        tableRepository.save(table);

        return ResponseEntity.ok(Map.of(
                "tableNumber", tableNumber,
                "newToken",    newToken,
                "newQrUrl",    newUrl,
                "message",     "QR regenerated. Print the new QR from /api/qr/table/" + tableNumber
        ));
    }
}
