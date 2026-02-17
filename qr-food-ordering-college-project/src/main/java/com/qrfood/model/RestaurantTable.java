package com.qrfood.model;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * RestaurantTable Entity
 *
 * Represents a physical dining table in the restaurant.
 * Each table gets a unique UUID token embedded in its QR code URL,
 * so every table's QR image is visually and cryptographically distinct.
 *
 * Maps to the 'restaurant_tables' table in PostgreSQL.
 *
 * QR URL format: http://localhost:8080/customer.html?table=3&token=<uuid>
 */
@Entity
@Table(name = "restaurant_tables")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number", nullable = false, unique = true)
    private int tableNumber;

    @Column(name = "qr_code_url", length = 500)
    private String qrCodeUrl;

    /**
     * Unique UUID token per table.
     * Generated once at creation and stored permanently.
     * Used to make each table's QR code unique.
     * Can be regenerated via POST /api/qr/regenerate/{tableNumber}
     */
    @Column(name = "qr_token", unique = true, length = 100)
    private String qrToken;

    @Column(name = "is_occupied", nullable = false)
    private boolean occupied = false;

    // --------------------------------------------------
    // Constructors
    // --------------------------------------------------

    public RestaurantTable() {}

    public RestaurantTable(int tableNumber) {
        this.tableNumber = tableNumber;
        this.qrToken     = UUID.randomUUID().toString();
    }

    // --------------------------------------------------
    // Getters and Setters
    // --------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getTableNumber() { return tableNumber; }
    public void setTableNumber(int tableNumber) { this.tableNumber = tableNumber; }

    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }

    public String getQrToken() { return qrToken; }
    public void setQrToken(String qrToken) { this.qrToken = qrToken; }

    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }

    @Override
    public String toString() {
        return "RestaurantTable{tableNumber=" + tableNumber + ", qrToken='" + qrToken + "'}";
    }
}
