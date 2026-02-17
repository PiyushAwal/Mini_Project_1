package com.qrfood.service;

import com.qrfood.model.MenuItem;
import com.qrfood.model.Order;
import com.qrfood.model.OrderItem;
import com.qrfood.repository.MenuItemRepository;
import com.qrfood.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * OrderService
 *
 * Core business logic for order management.
 * Handles order placement, status updates, and real-time
 * WebSocket notifications to kitchen and manager screens.
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // --------------------------------------------------
    // Place a new order
    // --------------------------------------------------

    /**
     * Places a new order from a customer at a table.
     *
     * @param tableNumber         The table number from QR scan
     * @param customerName        Customer's name (optional)
     * @param cartItems           List of {menuItemId, quantity, note}
     * @param specialInstructions General note for the whole order
     * @return Saved Order with generated ID
     */
    @Transactional
    public Order placeOrder(
            int tableNumber,
            String customerName,
            List<Map<String, Object>> cartItems,
            String specialInstructions) {

        Order order = new Order();
        order.setTableNumber(tableNumber);
        order.setCustomerName(customerName);
        order.setSpecialInstructions(specialInstructions);
        order.setStatus(Order.OrderStatus.PENDING);

        // Build order items from cart
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            Long menuItemId = Long.parseLong(cartItem.get("menuItemId").toString());
            int  quantity   = Integer.parseInt(cartItem.get("quantity").toString());
            String note     = cartItem.containsKey("note") ? cartItem.get("note").toString() : "";

            MenuItem menuItem = menuItemRepository.findById(menuItemId)
                    .orElseThrow(() -> new RuntimeException("Menu item not found: " + menuItemId));

            OrderItem oi = new OrderItem(menuItem, quantity);
            oi.setOrder(order);
            oi.setSpecialNote(note);
            return oi;
        }).toList();

        order.setItems(orderItems);

        // Calculate total
        BigDecimal total = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);

        // Push to kitchen and manager via WebSocket
        messagingTemplate.convertAndSend("/topic/kitchen", saved);
        messagingTemplate.convertAndSend("/topic/manager", saved);

        return saved;
    }

    // --------------------------------------------------
    // Update order status
    // --------------------------------------------------

    /**
     * Updates the status of an order.
     * Notifies all relevant screens via WebSocket.
     *
     * @param orderId   ID of the order to update
     * @param newStatus New status to set
     * @return Updated order
     */
    @Transactional
    public Order updateStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);

        // Push to all screens
        messagingTemplate.convertAndSend("/topic/kitchen", updated);
        messagingTemplate.convertAndSend("/topic/manager", updated);
        messagingTemplate.convertAndSend("/topic/table/" + updated.getTableNumber(), updated);

        return updated;
    }

    // --------------------------------------------------
    // Query methods
    // --------------------------------------------------

    /**
     * Returns active kitchen orders: PENDING, CONFIRMED, PREPARING.
     * Sorted oldest-first so oldest orders are served first.
     */
    public List<Order> getKitchenOrders() {
        return orderRepository.findByStatusInOrderByOrderTimeAsc(
                Arrays.asList(
                        Order.OrderStatus.PENDING,
                        Order.OrderStatus.CONFIRMED,
                        Order.OrderStatus.PREPARING
                )
        );
    }

    /**
     * Returns ALL orders sorted newest-first.
     * Used by manager dashboard.
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderTimeDesc();
    }

    /**
     * Returns all orders for a specific table, newest-first.
     */
    public List<Order> getTableOrders(int tableNumber) {
        return orderRepository.findByTableNumberOrderByOrderTimeDesc(tableNumber);
    }

    /**
     * Returns a single order by ID.
     */
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Calculates the total unpaid bill for a table.
     * Excludes CANCELLED and PAID orders.
     */
    public BigDecimal getTableBill(int tableNumber) {
        return orderRepository.calculateTableBill(tableNumber);
    }
}
