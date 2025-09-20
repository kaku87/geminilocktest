DROP TABLE IF EXISTS product;

CREATE TABLE product (
    id BIGINT NOT NULL,
    tenant_id VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    price DECIMAL(10, 2),
    version INT,
    created_by VARCHAR(255),
    created_time TIMESTAMP,
    updated_by VARCHAR(255),
    updated_time TIMESTAMP,
    is_deleted INT,
    quantity INT,
    zzcmn_fdate TIMESTAMP,
    PRIMARY KEY (id, tenant_id)
);