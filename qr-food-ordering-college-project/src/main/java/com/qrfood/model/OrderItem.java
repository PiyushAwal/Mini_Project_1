package com.qrfood.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

/**
 * OrderItem Entity
 *
 * Represents a single line item within an Order.
 * e.g. "2x Butter Chicken @ ₹320 = ₹640"
 *
 * Maps to the 'order_items' table in PostgreSQL.
 * Price is snapshotted at time of ordering so menu price changes
 * do not affect existing orders.
 */
@Entity
@Table(
    name = "order_items",
    indexes = {
        @Index(name = "idx_order_items_order_id", columnList = "order_id")
    }
)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Min(1)
    @Column(nullable = false)
    private int quantity;

    /**
     * Price per unit at the time of ordering (snapshot).
     * This does NOT change if the menu price is later updated.
     */
    @Column(name = "item_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal itemPrice;

    /**
     * itemPrice * quantity
     */
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "special_note", length = 300)
    private String specialNote;

    // --------------------------------------------------
    // Constructors
    // --------------------------------------------------

    public OrderItem() {}

    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem  = menuItem;
        this.quantity  = quantity;
        this.itemPrice = menuItem.getPrice();
        this.subtotal  = menuItem.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    // --------------------------------------------------
    // Getters and Setters
    // --------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getItemPrice() { return itemPrice; }
    public void setItemPrice(BigDecimal itemPrice) { this.itemPrice = itemPrice; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public String getSpecialNote() { return specialNote; }
    public void setSpecialNote(String specialNote) { this.specialNote = specialNote; }

    @Override
    public String toString() {
        return "OrderItem{qty=" + quantity + ", item=" + (menuItem != null ? menuItem.getName() : "null") + ", subtotal=" + subtotal + "}";
    }
}
