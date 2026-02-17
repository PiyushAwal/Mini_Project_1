package com.qrfood.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * MenuItem Entity
 *
 * Represents a food or drink item on the restaurant menu.
 * Maps to the 'menu_items' table in PostgreSQL.
 *
 * Categories: STARTER | MAIN | DESSERT | DRINK
 */
@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500)
    private String description;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    // --------------------------------------------------
    // Constructors
    // --------------------------------------------------

    public MenuItem() {}

    public MenuItem(String name, String description, BigDecimal price, String category) {
        this.name        = name;
        this.description = description;
        this.price       = price;
        this.category    = category;
    }

    // --------------------------------------------------
    // Getters and Setters
    // --------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return "MenuItem{id=" + id + ", name='" + name + "', price=" + price + ", category='" + category + "'}";
    }
}
