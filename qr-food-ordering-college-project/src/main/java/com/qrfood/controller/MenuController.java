package com.qrfood.controller;

import com.qrfood.model.MenuItem;
import com.qrfood.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MenuController
 *
 * REST API for menu item management.
 * Base URL: /api/menu
 *
 * Endpoints:
 *   GET    /api/menu                  -> All available items
 *   GET    /api/menu/categories       -> Items grouped by category
 *   GET    /api/menu/{id}             -> Single item by ID
 *   POST   /api/menu                  -> Add new item (manager)
 *   PUT    /api/menu/{id}             -> Update item (manager)
 *   DELETE /api/menu/{id}             -> Delete item (manager)
 *   PATCH  /api/menu/{id}/toggle      -> Toggle availability
 */
@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*")
public class MenuController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    // --------------------------------------------------
    // GET /api/menu
    // Returns all available menu items
    // --------------------------------------------------
    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        List<MenuItem> items = menuItemRepository.findByAvailableTrue();
        return ResponseEntity.ok(items);
    }

    // --------------------------------------------------
    // GET /api/menu/categories
    // Returns menu items grouped by category
    // Response: { "STARTER": [...], "MAIN": [...], ... }
    // --------------------------------------------------
    @GetMapping("/categories")
    public ResponseEntity<Map<String, List<MenuItem>>> getMenuByCategory() {
        List<MenuItem> items = menuItemRepository.findByAvailableTrue();
        Map<String, List<MenuItem>> grouped = items.stream()
                .collect(Collectors.groupingBy(MenuItem::getCategory));
        return ResponseEntity.ok(grouped);
    }

    // --------------------------------------------------
    // GET /api/menu/{id}
    // Returns a single menu item by ID
    // --------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        return menuItemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --------------------------------------------------
    // POST /api/menu
    // Add a new menu item
    // Body: { "name":"...", "description":"...", "price":120, "category":"STARTER" }
    // --------------------------------------------------
    @PostMapping
    public ResponseEntity<MenuItem> addMenuItem(@RequestBody MenuItem item) {
        MenuItem saved = menuItemRepository.save(item);
        return ResponseEntity.ok(saved);
    }

    // --------------------------------------------------
    // PUT /api/menu/{id}
    // Update an existing menu item
    // --------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(
            @PathVariable Long id,
            @RequestBody MenuItem updated) {

        return menuItemRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setDescription(updated.getDescription());
            existing.setPrice(updated.getPrice());
            existing.setCategory(updated.getCategory());
            existing.setAvailable(updated.isAvailable());
            existing.setImageUrl(updated.getImageUrl());
            return ResponseEntity.ok(menuItemRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // --------------------------------------------------
    // DELETE /api/menu/{id}
    // Remove a menu item
    // --------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        if (!menuItemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        menuItemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------------
    // PATCH /api/menu/{id}/toggle
    // Toggle availability (mark as out of stock / available)
    // --------------------------------------------------
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<MenuItem> toggleAvailability(@PathVariable Long id) {
        return menuItemRepository.findById(id).map(item -> {
            item.setAvailable(!item.isAvailable());
            return ResponseEntity.ok(menuItemRepository.save(item));
        }).orElse(ResponseEntity.notFound().build());
    }
}
