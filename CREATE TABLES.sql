CREATE TYPE address_type_enum AS ENUM (
    'HOME',
    'WORK',
    'BILLING',
    'SHIPPING'
);

CREATE TYPE payment_type_enum AS ENUM (
    'CREDIT_CARD',
    'DEBIT_CARD',
    'PAYPAL',
    'MBWAY',
    'BANK_TRANSFER'
);

CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,

    customer_code VARCHAR(30) NOT NULL UNIQUE,

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,

    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(30),

    tax_number VARCHAR(50),

    birth_date DATE,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customer_addresses (
    id BIGSERIAL PRIMARY KEY,

    customer_id BIGINT NOT NULL,

    address_type address_type_enum NOT NULL,

    street VARCHAR(255) NOT NULL,
    door_number VARCHAR(50),

    postal_code VARCHAR(20) NOT NULL,

    city VARCHAR(100) NOT NULL,
    district VARCHAR(100),

    country VARCHAR(100) NOT NULL,

    is_default BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_customer_addresses_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
);


CREATE TABLE customer_payment_methods (
    id BIGSERIAL PRIMARY KEY,

    customer_id BIGINT NOT NULL,

    payment_type payment_type_enum NOT NULL,

    card_holder VARCHAR(150),

    card_last_digits VARCHAR(4),

    expiry_month INTEGER,
    expiry_year INTEGER,

    iban VARCHAR(50),

    is_default BOOLEAN NOT NULL DEFAULT FALSE,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_customer_payment_methods_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
);


CREATE TABLE manufacturers (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(200) NOT NULL,

    website VARCHAR(500),

    support_email VARCHAR(255),

    country VARCHAR(100),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE product_categories (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(100) NOT NULL UNIQUE,

    description TEXT,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE product_origins (
    id BIGSERIAL PRIMARY KEY,

    country VARCHAR(100) NOT NULL,

    city VARCHAR(100),

    factory_name VARCHAR(255),

    factory_code VARCHAR(100),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,

    sku VARCHAR(50) NOT NULL UNIQUE,

    barcode VARCHAR(50),

    manufacturer_id BIGINT NOT NULL,

    category_id BIGINT NOT NULL,

    origin_id BIGINT,

    name VARCHAR(255) NOT NULL,

    short_description VARCHAR(500),

    description TEXT,

    price NUMERIC(12,2) NOT NULL
        CHECK (price >= 0),

    weight NUMERIC(10,3)
        CHECK (weight >= 0),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_products_manufacturer
        FOREIGN KEY (manufacturer_id)
        REFERENCES manufacturers(id),

    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id)
        REFERENCES product_categories(id),

    CONSTRAINT fk_products_origin
        FOREIGN KEY (origin_id)
        REFERENCES product_origins(id)
);


CREATE INDEX idx_customers_email
    ON customers(email);

CREATE INDEX idx_customers_customer_code
    ON customers(customer_code);

CREATE INDEX idx_customer_addresses_customer
    ON customer_addresses(customer_id);

CREATE INDEX idx_customer_payment_methods_customer
    ON customer_payment_methods(customer_id);

CREATE INDEX idx_products_sku
    ON products(sku);

CREATE INDEX idx_products_name
    ON products(name);

CREATE INDEX idx_products_manufacturer
    ON products(manufacturer_id);

CREATE INDEX idx_products_category
    ON products(category_id);

CREATE INDEX idx_products_origin
    ON products(origin_id);


