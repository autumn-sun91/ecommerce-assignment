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
    user_id    BIGINT   NOT NULL,
    product_id BIGINT   NOT NULL,
    quantity   INT      NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_orders_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE INDEX idx_orders_user_product ON orders(user_id, product_id);

CREATE INDEX idx_orders_product ON orders(product_id);