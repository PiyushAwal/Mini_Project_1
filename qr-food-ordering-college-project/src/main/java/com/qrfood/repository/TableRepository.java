package com.qrfood.repository;

import com.qrfood.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * TableRepository
 *
 * Spring Data JPA repository for RestaurantTable entity.
 * Maps to the 'restaurant_tables' table in PostgreSQL.
 */
@Repository
public interface TableRepository extends JpaRepository<RestaurantTable, Long> {

    /**
     * Find a table by its table number.
     * Used when generating or regenerating QR codes.
     */
    Optional<RestaurantTable> findByTableNumber(int tableNumber);

    /**
     * Find a table by its unique QR token.
     * Used to validate incoming QR scans.
     */
    Optional<RestaurantTable> findByQrToken(String qrToken);
}
