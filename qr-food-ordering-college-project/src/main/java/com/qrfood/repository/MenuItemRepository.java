package com.qrfood.repository;

import com.qrfood.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MenuItemRepository
 *
 * Spring Data JPA repository for MenuItem entity.
 * Provides CRUD operations + custom queries for PostgreSQL.
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    /**
     * Get all menu items that are currently available.
     * Used by customer menu page.
     */
    List<MenuItem> findByAvailableTrue();

    /**
     * Get available items filtered by category.
     * Used for category-specific views.
     */
    List<MenuItem> findByCategoryAndAvailableTrue(String category);

    /**
     * Get all items in a category (including unavailable).
     * Used by manager to manage menu.
     */
    List<MenuItem> findByCategory(String category);
}
