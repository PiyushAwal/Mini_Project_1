-- =================================================
-- QR Food Ordering System - PostgreSQL Schema
-- Java 21 | Spring Boot 3.3.5 | PostgreSQL 15+
-- =================================================
-- Run this manually in psql or pgAdmin:
--   psql -U postgres -d qrfooddb -f schema.sql
-- OR let Hibernate auto-create (ddl-auto=update)
-- =================================================

-- Drop tables in reverse dependency order
DROP TABLE IF EXISTS order_items       CASCADE;
DROP TABLE IF EXISTS orders            CASCADE;
DROP TABLE IF EXISTS menu_items        CASCADE;
DROP TABLE IF EXISTS restaurant_tables CASCADE;

-- =================================================
-- TABLE: restaurant_tables
-- One row per physical table in the restaurant.
-- Each table gets a unique UUID token for its QR code.
-- =================================================
CREATE TABLE restaurant_tables (
    id           BIGSERIAL    PRIMARY KEY,
    table_number INTEGER      NOT NULL UNIQUE,
    qr_code_url  VARCHAR(500),
    qr_token     VARCHAR(100) UNIQUE,
    is_occupied  BOOLEAN      NOT NULL DEFAULT FALSE
);

-- =================================================
-- TABLE: menu_items
-- All food and drink items available to order.
-- =================================================
CREATE TABLE menu_items (
    id           BIGSERIAL      PRIMARY KEY,
    name         VARCHAR(200)   NOT NULL,
    description  VARCHAR(500),
    price        NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    category     VARCHAR(50)    NOT NULL,
    image_url    VARCHAR(500),
    is_available BOOLEAN        NOT NULL DEFAULT TRUE
);

-- =================================================
-- TABLE: orders
-- One order record per customer order session.
-- A table can have multiple orders in a session.
-- =================================================
CREATE TABLE orders (
    id                   BIGSERIAL      PRIMARY KEY,
    table_number         INTEGER        NOT NULL,
    customer_name        VARCHAR(200),
    status               VARCHAR(30)    NOT NULL DEFAULT 'PENDING',
    total_amount         NUMERIC(10, 2) CHECK (total_amount >= 0),
    order_time           TIMESTAMP      NOT NULL DEFAULT NOW(),
    special_instructions VARCHAR(500),

    CONSTRAINT chk_order_status CHECK (
        status IN ('PENDING','CONFIRMED','PREPARING','READY','SERVED','CANCELLED','PAID')
    )
);

CREATE INDEX idx_orders_status     ON orders (status);
CREATE INDEX idx_orders_table      ON orders (table_number);
CREATE INDEX idx_orders_time       ON orders (order_time DESC);

-- =================================================
-- TABLE: order_items
-- Line items inside each order.
-- =================================================
CREATE TABLE order_items (
    id           BIGSERIAL      PRIMARY KEY,
    order_id     BIGINT         NOT NULL REFERENCES orders(id)    ON DELETE CASCADE,
    menu_item_id BIGINT         NOT NULL REFERENCES menu_items(id),
    quantity     INTEGER        NOT NULL CHECK (quantity > 0),
    item_price   NUMERIC(10, 2) NOT NULL CHECK (item_price >= 0),
    subtotal     NUMERIC(10, 2) NOT NULL CHECK (subtotal >= 0),
    special_note VARCHAR(300)
);

CREATE INDEX idx_order_items_order_id ON order_items (order_id);

-- =================================================
-- SAMPLE MENU DATA
-- (DataLoader.java also seeds this on first startup)
-- =================================================
INSERT INTO menu_items (name, description, price, category) VALUES
-- STARTERS
('Veg Spring Rolls',  'Crispy rolls filled with mixed vegetables and herbs',       120.00, 'STARTER'),
('Chicken Tikka',     'Tender chicken marinated in spices, grilled to perfection', 220.00, 'STARTER'),
('Paneer Tikka',      'Cottage cheese cubes marinated and grilled',                180.00, 'STARTER'),
('Onion Rings',       'Golden crispy onion rings with dipping sauce',              100.00, 'STARTER'),

-- MAINS
('Butter Chicken',    'Classic creamy tomato-based chicken curry',                 320.00, 'MAIN'),
('Dal Makhani',       'Slow-cooked black lentils with butter and cream',           240.00, 'MAIN'),
('Palak Paneer',      'Fresh cottage cheese in smooth spinach gravy',              260.00, 'MAIN'),
('Veg Biryani',       'Fragrant basmati rice cooked with seasonal vegetables',     280.00, 'MAIN'),
('Chicken Biryani',   'Aromatic rice dish cooked with tender chicken pieces',      350.00, 'MAIN'),
('Naan Bread',        'Freshly baked leavened flatbread (2 pcs)',                   60.00, 'MAIN'),

-- DESSERTS
('Gulab Jamun',       'Soft milk-solid balls soaked in rose syrup (3 pcs)',         90.00, 'DESSERT'),
('Mango Kulfi',       'Traditional Indian ice cream with fresh mango',             110.00, 'DESSERT'),
('Chocolate Brownie', 'Warm fudgy brownie with vanilla ice cream',                 150.00, 'DESSERT'),

-- DRINKS
('Mango Lassi',       'Thick yogurt-based mango drink',                             80.00, 'DRINK'),
('Fresh Lime Soda',   'Refreshing lime with soda, sweet or salted',                 60.00, 'DRINK'),
('Masala Chai',       'Indian spiced tea with milk',                                40.00, 'DRINK'),
('Cold Coffee',       'Chilled coffee blended with milk and sugar',                 90.00, 'DRINK');

-- Tables (QR tokens assigned by app on first startup)
INSERT INTO restaurant_tables (table_number) VALUES
(1),(2),(3),(4),(5),(6),(7),(8),(9),(10);

-- Verify row counts
SELECT 'restaurant_tables' AS "Table", COUNT(*) AS "Rows" FROM restaurant_tables
UNION ALL
SELECT 'menu_items',                   COUNT(*) FROM menu_items
UNION ALL
SELECT 'orders',                       COUNT(*) FROM orders
UNION ALL
SELECT 'order_items',                  COUNT(*) FROM order_items;
