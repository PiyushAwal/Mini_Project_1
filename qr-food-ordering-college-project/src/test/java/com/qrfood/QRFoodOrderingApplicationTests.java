package com.qrfood;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * QRFoodOrderingApplicationTests
 *
 * Basic Spring Boot context load test.
 * Verifies the application context starts up correctly.
 *
 * Note: Requires a running PostgreSQL instance.
 * Set test DB credentials in src/test/resources/application-test.properties
 */
@SpringBootTest
@ActiveProfiles("test")
class QRFoodOrderingApplicationTests {

    @Test
    void contextLoads() {
        // Verifies the Spring application context loads without errors
    }
}
