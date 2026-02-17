package com.qrfood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * QRFoodOrderingApplication
 *
 * Main entry point for the QR-Based Food Ordering System.
 *
 * Tech Stack:
 *   - Java 21 (OpenJDK)
 *   - Spring Boot 3.3.5
 *   - PostgreSQL (via Spring Data JPA / Hibernate)
 *   - WebSocket + STOMP (real-time kitchen/manager updates)
 *   - ZXing (QR code generation)
 *
 * Run: mvn spring-boot:run
 * Then open:
 *   Customer : http://localhost:8080/customer.html?table=1
 *   Kitchen  : http://localhost:8080/kitchen.html
 *   Manager  : http://localhost:8080/manager.html
 */
@SpringBootApplication
public class QRFoodOrderingApplication {

    public static void main(String[] args) {
        SpringApplication.run(QRFoodOrderingApplication.class, args);
    }
}
