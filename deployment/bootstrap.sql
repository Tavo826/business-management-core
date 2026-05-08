-- Idempotent DDL for Business Manager Core.
-- Runs against the database created by docker-compose (POSTGRES_DB=${CORE_DB_NAME}).

CREATE TABLE IF NOT EXISTS users (
    document_id VARCHAR(20) PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL,
    surname     VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) UNIQUE NOT NULL,
    password    VARCHAR(100) NOT NULL,
    birthdate   VARCHAR(20),
    created_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS business (
    nit                VARCHAR(25)  PRIMARY KEY,
    name               VARCHAR(100) NOT NULL,
    description        TEXT,
    phone              VARCHAR(20),
    email              VARCHAR(50),
    address            VARCHAR(50),
    owner_document_id  VARCHAR(20)  NOT NULL,
    phone_number_id    VARCHAR(20)  NOT NULL,
    social_media_list  JSONB DEFAULT '[]'::jsonb,
    bank_account_list  JSONB DEFAULT '[]'::jsonb,
    created_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_owner_user FOREIGN KEY (owner_document_id) REFERENCES users(document_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS orders (
    id               VARCHAR(50) PRIMARY KEY,
    business_id      VARCHAR(20),
    customer_name    VARCHAR(50),
    customer_phone   VARCHAR(20),
    customer_address VARCHAR(200),
    status           VARCHAR(10),
    total_amount     NUMERIC(10,2),
    created_at       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id           VARCHAR(50) PRIMARY KEY,
    order_id     VARCHAR(50) NOT NULL,
    product_name VARCHAR(100),
    quantity     INTEGER NOT NULL,
    unit_price   NUMERIC(10,2),
    CONSTRAINT fk_order FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS client_consent (
    id              VARCHAR(60)  PRIMARY KEY,
    business_nit    VARCHAR(25)  NOT NULL,
    customer_phone  VARCHAR(20)  NOT NULL,
    policy_version  VARCHAR(20)  NOT NULL,
    policy_url      VARCHAR(255) NOT NULL,
    notice_sent_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_client_consent_business_phone UNIQUE (business_nit, customer_phone)
);
