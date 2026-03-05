CREATE DATABASE IF NOT EXISTS shop;
USE shop;

CREATE TABLE product
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    stock      INT      NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE orders
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT      NOT NULL,
    product_id BIGINT      NOT NULL,
    quantity   INT         NOT NULL,
    status     VARCHAR(30) NOT NULL,
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_orders_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE INDEX idx_orders_user_product ON orders (user_id, product_id);

CREATE INDEX idx_orders_product ON orders (product_id);

CREATE TABLE delivery_histories
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id         BIGINT       NOT NULL UNIQUE,
    idempotency_key  VARCHAR(255) NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'UNKNOWN',
    tracking_number  VARCHAR(255),
    fail_reason      TEXT,
    attempt_count    INT          NOT NULL DEFAULT 0,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_status (status),
    INDEX idx_order_id (order_id)
);