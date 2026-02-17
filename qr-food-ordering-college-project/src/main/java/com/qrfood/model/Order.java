package com.qrfood.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Entity
 *
 * Represents a single order placed by a customer at a table.
 * One table can have multiple orders during a session.
 * Maps to the 'orders' table in PostgreSQL.
 *
 * Status Flow:
 *   PENDING → CONFIRMED → PREPARING → READY → SERVED → PAID
 *                                                ↓
 *                                           CANCELLED
 */
@Entity
@Table(
    name = "orders",
    indexes = {
        @Index(name = "idx_orders_status",     columnList = "status"),
        @Index(name = "idx_orders_table",      columnList = "table_number"),
        @Index(name = "idx_orders_order_time", columnList = "order_time")
    }
)
public class Order {

    /**
     * All possible states of an order.
     */
    public enum OrderStatus {
        PENDING,    // Customer placed order, waiting for kitchen
        CONFIRMED,  // Kitchen accepted the order
        PREPARING,  // Currently being cooked
        READY,      // Ready to be served at table
        SERVED,     // Delivered to the table
        CANCELLED,  // Order was cancelled
        PAID        // Bill has been paid
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number", nullable = false)
    private int tableNumber;

    @Column(name = "customer_name", length = 200)
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;

    @Column(name = "special_instructions", length = 500)
    private String specialInstructions;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    // --------------------------------------------------
    // Lifecycle hooks
    // --------------------------------------------------

    @PrePersist
    protected void onCreate() {
        this.orderTime = LocalDateTime.now();
    }

    // --------------------------------------------------
    // Constructors
    // --------------------------------------------------

    public Order() {}

    // --------------------------------------------------
    // Getters and Setters
    // --------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getTableNumber() { return tableNumber; }
    public void setTableNumber(int tableNumber) { this.tableNumber = tableNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    @Override
    public String toString() {
        return "Order{id=" + id + ", table=" + tableNumber + ", status=" + status + ", total=" + totalAmount + "}";
    }
}
