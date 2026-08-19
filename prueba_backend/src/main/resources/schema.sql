-- =============================================
-- BASE DE DATOS: DDL - Definición de Esquema PostgreSQL
-- Entidad Financiera - Clientes, Cuentas y Transacciones
-- =============================================

-- 1. Eliminar tablas si existen (respetando orden de llaves foráneas)
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS accounts CASCADE;
DROP TABLE IF EXISTS clients CASCADE;

-- 2. Tabla: CLIENTES (clients)
CREATE TABLE clients (
    id                    BIGSERIAL       PRIMARY KEY,
    identification_type   VARCHAR(20)     NOT NULL,
    identification_number BIGINT          NOT NULL,
    first_name            VARCHAR(100)    NOT NULL,
    last_name             VARCHAR(100)    NOT NULL,
    email                 VARCHAR(150)    NOT NULL,
    birth_date            DATE            NOT NULL,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Restricciones (Constraints)
    CONSTRAINT uq_client_identification UNIQUE (identification_number),
    CONSTRAINT chk_client_identification_type CHECK (identification_type IN ('CC', 'CE', 'PASSPORT')),
    CONSTRAINT chk_client_first_name_length CHECK (LENGTH(first_name) >= 2),
    CONSTRAINT chk_client_last_name_length CHECK (LENGTH(last_name) >= 2)
);

-- Índices para búsqueda rápida de clientes
CREATE INDEX idx_clients_identification_number ON clients (identification_number);
CREATE INDEX idx_clients_email ON clients (email);

-- 3. Tabla: CUENTAS / PRODUCTOS FINANCIEROS (accounts)
CREATE TABLE accounts (
    id                BIGSERIAL       PRIMARY KEY,
    account_type      VARCHAR(10)     NOT NULL,
    account_number    VARCHAR(10)     NOT NULL,
    status            VARCHAR(10)     NOT NULL DEFAULT 'ACTIVE',
    balance           DECIMAL(15, 2)  NOT NULL DEFAULT 0.00,
    gmf_exempt        BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    client_id         BIGINT          NOT NULL,
    version           BIGINT          NOT NULL DEFAULT 0,

    -- Llave foránea
    CONSTRAINT fk_account_client FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE RESTRICT,

    -- Restricciones (Constraints)
    CONSTRAINT uq_account_number UNIQUE (account_number),
    CONSTRAINT chk_account_type CHECK (account_type IN ('SAVINGS', 'CHECKING')),
    CONSTRAINT chk_account_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'CANCELLED')),
    CONSTRAINT chk_account_number_length CHECK (LENGTH(account_number) = 10),
    CONSTRAINT chk_account_number_prefix CHECK (
        (account_type = 'SAVINGS' AND account_number LIKE '53%') OR
        (account_type = 'CHECKING' AND account_number LIKE '33%')
    ),
    CONSTRAINT chk_savings_balance CHECK (account_type != 'SAVINGS' OR balance >= 0)
);

-- Índices para optimización de consultas de cuentas
CREATE INDEX idx_accounts_client_id ON accounts (client_id);
CREATE INDEX idx_accounts_account_number ON accounts (account_number);
CREATE INDEX idx_accounts_status ON accounts (status);

-- 4. Tabla: TRANSACCIONES (transactions)
CREATE TABLE transactions (
    id                  BIGSERIAL       PRIMARY KEY,
    transaction_type    VARCHAR(15)     NOT NULL,
    amount              DECIMAL(15, 2)  NOT NULL,
    description         VARCHAR(255),
    source_account_id   BIGINT,
    target_account_id   BIGINT,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Llaves foráneas
    CONSTRAINT fk_transaction_source_account FOREIGN KEY (source_account_id) REFERENCES accounts(id) ON DELETE RESTRICT,
    CONSTRAINT fk_transaction_target_account FOREIGN KEY (target_account_id) REFERENCES accounts(id) ON DELETE RESTRICT,

    -- Restricciones (Constraints)
    CONSTRAINT chk_transaction_type CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER')),
    CONSTRAINT chk_transaction_amount CHECK (amount > 0),
    CONSTRAINT chk_transaction_accounts CHECK (
        (transaction_type = 'DEPOSIT' AND target_account_id IS NOT NULL) OR
        (transaction_type = 'WITHDRAWAL' AND source_account_id IS NOT NULL) OR
        (transaction_type = 'TRANSFER' AND source_account_id IS NOT NULL AND target_account_id IS NOT NULL)
    )
);

-- Índices para reportes y extractos bancarios
CREATE INDEX idx_transactions_source_account ON transactions (source_account_id);
CREATE INDEX idx_transactions_target_account ON transactions (target_account_id);
CREATE INDEX idx_transactions_created_at ON transactions (created_at);
