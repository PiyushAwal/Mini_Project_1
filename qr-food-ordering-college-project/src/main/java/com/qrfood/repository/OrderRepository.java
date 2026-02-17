package com.qrfood.repository;

import com.qrfood.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * OrderRepository
 *
 * Spring Data JPA repository for Order entity.
 * Maps to the 'orders' table in PostgreSQL.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Get all orders for a specific table, newest first.
     * Used by customer screen to track their orders.
     */
    List<Order> findByTableNumberOrderByOrderTimeDesc(int tableNumber);

    /**
     * Get orders matching a specific status, oldest first.
     * Used for kitchen / manager filtering.
     */
    List<Order> findByStatusOrderByOrderTimeAsc(Order.OrderStatus status);

    /**
     * Get orders matching multiple statuses, oldest first.
     * Used by kitchen screen to show PENDING + CONFIRMED + PREPARING.
     */
    List<Order> findByStatusInOrderByOrderTimeAsc(List<Order.OrderStatus> statuses);

    /**
     * Get all orders, newest first.
     * Used by manager dashboard for full order history.
     */
    List<Order> findAllByOrderByOrderTimeDesc();

    /**
     * Calculate total unpaid bill for a table.
     * Sums totalAmount of all orders that are not CANCELLED or PAID.
     */
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.tableNumber = :tableNumber
          AND o.status NOT IN ('CANCELLED', 'PAID')
    """)
    BigDecimal calculateTableBill(@Param("tableNumber") int tableNumber);
}
