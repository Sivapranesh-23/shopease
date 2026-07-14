-- ============================================================
-- Shopease V1 — Schema
-- MySQL 8 / InnoDB
-- ============================================================

-- -----------------------------------------------------------
-- Users
-- -----------------------------------------------------------
CREATE TABLE users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(120)  NOT NULL,
    email           VARCHAR(180)  NOT NULL,
    password        VARCHAR(255)  NOT NULL,
    role            VARCHAR(16)   NOT NULL DEFAULT 'CUSTOMER',
    active          TINYINT(1)    NOT NULL DEFAULT 1,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- Categories
-- -----------------------------------------------------------
CREATE TABLE categories (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100)  NOT NULL,
    slug            VARCHAR(120)  NOT NULL,
    description     VARCHAR(280)  NULL,
    image_url       VARCHAR(600)  NULL,
    parent_id       BIGINT        NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_categories_slug UNIQUE (slug),
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories (id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- Products
-- -----------------------------------------------------------
CREATE TABLE products (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(180)  NOT NULL,
    slug              VARCHAR(200)  NOT NULL,
    sku               VARCHAR(64)   NOT NULL,
    summary           VARCHAR(600)  NULL,
    description       TEXT          NULL,
    price             DECIMAL(10,2) NOT NULL,
    compare_at_price  DECIMAL(10,2) NULL,
    stock             INT           NOT NULL DEFAULT 0,
    image_url         VARCHAR(600)  NULL,
    is_active         TINYINT(1)    NOT NULL DEFAULT 1,
    rating            DOUBLE        NOT NULL DEFAULT 0.0,
    review_count      INT           NOT NULL DEFAULT 0,
    category_id       BIGINT        NOT NULL,
    created_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_products_slug  UNIQUE (slug),
    CONSTRAINT uk_products_sku   UNIQUE (sku),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT,

    INDEX idx_products_active (is_active),
    INDEX idx_products_category (category_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- Addresses
-- -----------------------------------------------------------
CREATE TABLE addresses (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    full_name       VARCHAR(120)  NOT NULL,
    phone           VARCHAR(20)   NOT NULL,
    line1           VARCHAR(200)  NOT NULL,
    line2           VARCHAR(200)  NULL,
    city            VARCHAR(100)  NOT NULL,
    postal_code     VARCHAR(20)   NOT NULL,
    country         VARCHAR(100)  NOT NULL,
    is_default      TINYINT(1)    NOT NULL DEFAULT 0,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- Reviews
-- -----------------------------------------------------------
CREATE TABLE reviews (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    product_id      BIGINT        NOT NULL,
    rating          INT           NOT NULL,
    comment         VARCHAR(1000) NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_reviews_user_product UNIQUE (user_id, product_id),
    CONSTRAINT fk_reviews_user    FOREIGN KEY (user_id)    REFERENCES users    (id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- Carts + Cart Items
-- -----------------------------------------------------------
CREATE TABLE carts (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_carts_user UNIQUE (user_id),
    CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE cart_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id         BIGINT        NOT NULL,
    product_id      BIGINT        NOT NULL,
    quantity        INT           NOT NULL,
    unit_price      DECIMAL(10,2) NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_cart_items_cart_product UNIQUE (cart_id, product_id),
    CONSTRAINT fk_cart_items_cart    FOREIGN KEY (cart_id)    REFERENCES carts    (id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- Orders + Order Items
-- -----------------------------------------------------------
CREATE TABLE orders (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number      VARCHAR(24)   NOT NULL,
    user_id           BIGINT        NOT NULL,
    status            VARCHAR(16)   NOT NULL DEFAULT 'PENDING',
    subtotal          DECIMAL(10,2) NOT NULL,
    shipping_total    DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tax_total         DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    grand_total       DECIMAL(10,2) NOT NULL,
    shipping_address  VARCHAR(600)  NULL,
    shipping_carrier  VARCHAR(60)   NULL,
    tracking_number   VARCHAR(80)   NULL,
    created_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_orders_number UNIQUE (order_number),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT,

    INDEX idx_orders_status (status),
    INDEX idx_orders_user   (user_id)
) ENGINE=InnoDB;

CREATE TABLE order_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id        BIGINT        NOT NULL,
    product_id      BIGINT        NULL,
    product_name    VARCHAR(180)  NOT NULL,
    product_sku     VARCHAR(64)   NULL,
    image_url       VARCHAR(600)  NULL,
    quantity        INT           NOT NULL,
    unit_price      DECIMAL(10,2) NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------
-- Payments
-- -----------------------------------------------------------
CREATE TABLE payments (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id              BIGINT        NOT NULL,
    provider              VARCHAR(32)   NOT NULL,
    provider_payment_id   VARCHAR(120)  NULL,
    amount                DECIMAL(10,2) NOT NULL,
    currency              VARCHAR(3)    NOT NULL DEFAULT 'USD',
    status                VARCHAR(16)   NOT NULL DEFAULT 'PENDING',
    metadata              VARCHAR(400)  NULL,
    created_at            TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_payments_order UNIQUE (order_id),
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE RESTRICT
) ENGINE=InnoDB;
