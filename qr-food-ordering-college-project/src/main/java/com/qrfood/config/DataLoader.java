package com.qrfood.config;

import com.qrfood.model.MenuItem;
import com.qrfood.model.RestaurantTable;
import com.qrfood.repository.MenuItemRepository;
import com.qrfood.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * DataLoader
 *
 * Seeds the PostgreSQL database with sample data on first startup.
 * Runs automatically after the application starts.
 *
 * Safe to run multiple times — checks COUNT(*) before inserting,
 * so it will skip seeding if data already exists.
 *
 * Seeded data:
 *   - 17 menu items (4 starters, 6 mains, 3 desserts, 4 drinks)
 *   - 10 restaurant tables with unique UUID QR tokens
 */
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private TableRepository tableRepository;

    @Override
    public void run(String... args) {
        seedMenuItems();
        seedTables();
    }

    // --------------------------------------------------
    // Seed Menu Items
    // --------------------------------------------------
    private void seedMenuItems() {
        if (menuItemRepository.count() > 0) {
            System.out.println("ℹ️  [DataLoader] Menu items already exist — skipping seed.");
            return;
        }

        // ---- STARTERS ----
        save("Veg Spring Rolls",
             "Crispy rolls filled with mixed vegetables and herbs",
             "120.00", "STARTER");
        save("Chicken Tikka",
             "Tender chicken marinated in spices, grilled to perfection",
             "220.00", "STARTER");
        save("Paneer Tikka",
             "Cottage cheese cubes marinated and grilled",
             "180.00", "STARTER");
        save("Onion Rings",
             "Golden crispy onion rings with dipping sauce",
             "100.00", "STARTER");

        // ---- MAINS ----
        save("Butter Chicken",
             "Classic creamy tomato-based chicken curry",
             "320.00", "MAIN");
        save("Dal Makhani",
             "Slow-cooked black lentils with butter and cream",
             "240.00", "MAIN");
        save("Palak Paneer",
             "Fresh cottage cheese in smooth spinach gravy",
             "260.00", "MAIN");
        save("Veg Biryani",
             "Fragrant basmati rice cooked with seasonal vegetables",
             "280.00", "MAIN");
        save("Chicken Biryani",
             "Aromatic rice dish cooked with tender chicken pieces",
             "350.00", "MAIN");
        save("Naan Bread",
             "Freshly baked leavened flatbread (2 pcs)",
             "60.00", "MAIN");

        // ---- DESSERTS ----
        save("Gulab Jamun",
             "Soft milk-solid balls soaked in rose syrup (3 pcs)",
             "90.00", "DESSERT");
        save("Mango Kulfi",
             "Traditional Indian ice cream with fresh mango",
             "110.00", "DESSERT");
        save("Chocolate Brownie",
             "Warm fudgy brownie with vanilla ice cream",
             "150.00", "DESSERT");

        // ---- DRINKS ----
        save("Mango Lassi",    "Thick yogurt-based mango drink",             "80.00", "DRINK");
        save("Fresh Lime Soda","Refreshing lime with soda, sweet or salted",  "60.00", "DRINK");
        save("Masala Chai",    "Indian spiced tea with milk",                  "40.00", "DRINK");
        save("Cold Coffee",    "Chilled coffee blended with milk and sugar",   "90.00", "DRINK");

        System.out.println("✅ [DataLoader] 17 menu items inserted into PostgreSQL.");
    }

    // --------------------------------------------------
    // Seed Tables
    // --------------------------------------------------
    private void seedTables() {
        if (tableRepository.count() > 0) {
            System.out.println("ℹ️  [DataLoader] Tables already exist — skipping seed.");
            return;
        }

        for (int i = 1; i <= 10; i++) {
            // RestaurantTable constructor auto-generates a UUID qrToken
            tableRepository.save(new RestaurantTable(i));
        }
        System.out.println("✅ [DataLoader] 10 tables created with unique QR tokens in PostgreSQL.");
    }

    // --------------------------------------------------
    // Helper
    // --------------------------------------------------
    private void save(String name, String description, String price, String category) {
        MenuItem item = new MenuItem(name, description, new BigDecimal(price), category);
        menuItemRepository.save(item);
    }
}
