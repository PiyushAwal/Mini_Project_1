# QR-Based Food Ordering System
### Java 21 | Spring Boot 3.3.5 | PostgreSQL | WebSocket

---

## Project Structure

```
qr-food-ordering/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/qrfood/
    │   │   ├── QRFoodOrderingApplication.java   <- Entry point
    │   │   ├── model/
    │   │   │   ├── MenuItem.java
    │   │   │   ├── RestaurantTable.java
    │   │   │   ├── Order.java
    │   │   │   └── OrderItem.java
    │   │   ├── repository/
    │   │   │   ├── MenuItemRepository.java
    │   │   │   ├── TableRepository.java
    │   │   │   └── OrderRepository.java
    │   │   ├── service/
    │   │   │   └── OrderService.java
    │   │   ├── controller/
    │   │   │   ├── MenuController.java
    │   │   │   ├── OrderController.java
    │   │   │   └── QRCodeController.java
    │   │   └── config/
    │   │       ├── WebSocketConfig.java
    │   │       └── DataLoader.java
    │   └── resources/
    │       ├── application.properties
    │       ├── schema.sql
    │       └── static/
    │           ├── customer.html
    │           ├── kitchen.html
    │           └── manager.html
    └── test/
        └── java/com/qrfood/
            └── QRFoodOrderingApplicationTests.java
```

---

## PostgreSQL Setup

```bash
# 1. Open psql
psql -U postgres

# 2. Create database
CREATE DATABASE qrfooddb;
\q

# 3. Update password in application.properties
spring.datasource.password=yourpassword

# 4. (Optional) Run schema manually
psql -U postgres -d qrfooddb -f src/main/resources/schema.sql
```

---

## Run the Application

```bash
mvn clean package
mvn spring-boot:run
```

---

## Access URLs

| Screen   | URL |
|----------|-----|
| Customer | http://localhost:8080/customer.html?table=1 |
| Kitchen  | http://localhost:8080/kitchen.html |
| Manager  | http://localhost:8080/manager.html |
| QR Code  | http://localhost:8080/api/qr/table/1 |

---

## API Reference

| Method | URL | Description |
|--------|-----|-------------|
| GET | /api/menu | All menu items |
| GET | /api/menu/categories | Menu by category |
| POST | /api/orders/place | Place order |
| GET | /api/orders/kitchen | Kitchen orders |
| GET | /api/orders/all | All orders |
| PATCH | /api/orders/{id}/status | Update status |
| GET | /api/orders/table/{n}/bill | Table bill |
| GET | /api/qr/table/{n} | QR code image |
