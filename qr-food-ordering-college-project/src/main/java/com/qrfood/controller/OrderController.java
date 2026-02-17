package com.qrfood.controller;

import com.qrfood.model.Order;
import com.qrfood.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * OrderController
 *
 * REST API for order management.
 * Base URL: /api/orders
 *
 * Endpoints:
 *   POST   /api/orders/place              -> Customer places order
 *   GET    /api/orders/kitchen            -> Kitchen active orders
 *   GET    /api/orders/all                -> All orders (manager)
 *   GET    /api/orders/{id}               -> Single order by ID
 *   GET    /api/orders/table/{n}          -> All orders for a table
 *   GET    /api/orders/table/{n}/bill     -> Total bill for a table
 *   PATCH  /api/orders/{id}/status        -> Update order status
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // --------------------------------------------------
    // POST /api/orders/place
    // Customer submits their cart as an order
    //
    // Request body:
    // {
    //   "tableNumber": 3,
    //   "customerName": "Rahul",
    //   "specialInstructions": "No onions",
    //   "items": [
    //     { "menuItemId": 1, "quantity": 2, "note": "Extra spicy" },
    //     { "menuItemId": 5, "quantity": 1, "note": "" }
    //   ]
    // }
    // --------------------------------------------------
    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(@RequestBody Map<String, Object> requestBody) {
        int    tableNumber  = Integer.parseInt(requestBody.get("tableNumber").toString());
        String customerName = requestBody.getOrDefault("customerName", "Guest").toString();
        String instructions = requestBody.getOrDefault("specialInstructions", "").toString();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items =
                (List<Map<String, Object>>) requestBody.get("items");

        Order saved = orderService.placeOrder(tableNumber, customerName, items, instructions);
        return ResponseEntity.ok(saved);
    }

    // --------------------------------------------------
    // GET /api/orders/kitchen
    // Active orders for kitchen screen
    // Returns: PENDING, CONFIRMED, PREPARING — oldest first
    // --------------------------------------------------
    @GetMapping("/kitchen")
    public ResponseEntity<List<Order>> getKitchenOrders() {
        return ResponseEntity.ok(orderService.getKitchenOrders());
    }

    // --------------------------------------------------
    // GET /api/orders/all
    // Full order history for manager dashboard
    // Returns: all orders, newest first
    // --------------------------------------------------
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // --------------------------------------------------
    // GET /api/orders/{id}
    // Get a specific order by its ID
    // --------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --------------------------------------------------
    // GET /api/orders/table/{tableNumber}
    // All orders for a specific table (newest first)
    // --------------------------------------------------
    @GetMapping("/table/{tableNumber}")
    public ResponseEntity<List<Order>> getTableOrders(@PathVariable int tableNumber) {
        return ResponseEntity.ok(orderService.getTableOrders(tableNumber));
    }

    // --------------------------------------------------
    // GET /api/orders/table/{tableNumber}/bill
    // Total outstanding bill for a table
    //
    // Response:
    // {
    //   "tableNumber": 3,
    //   "totalBill": 860.00,
    //   "orders": [...]
    // }
    // --------------------------------------------------
    @GetMapping("/table/{tableNumber}/bill")
    public ResponseEntity<Map<String, Object>> getTableBill(@PathVariable int tableNumber) {
        BigDecimal   total  = orderService.getTableBill(tableNumber);
        List<Order>  orders = orderService.getTableOrders(tableNumber);
        return ResponseEntity.ok(Map.of(
                "tableNumber", tableNumber,
                "totalBill",   total,
                "orders",      orders
        ));
    }

    // --------------------------------------------------
    // PATCH /api/orders/{id}/status
    // Update order status (by kitchen or manager)
    //
    // Request body: { "status": "CONFIRMED" }
    // Valid values: PENDING | CONFIRMED | PREPARING | READY | SERVED | CANCELLED | PAID
    // --------------------------------------------------
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        Order.OrderStatus newStatus = Order.OrderStatus.valueOf(body.get("status"));
        Order updated = orderService.updateStatus(id, newStatus);
        return ResponseEntity.ok(updated);
    }
}
