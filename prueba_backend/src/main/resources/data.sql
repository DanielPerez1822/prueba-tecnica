-- =============================================
-- BASE DE DATOS: DML - Datos Iniciales de Prueba (Seeds)
-- Entidad Financiera - Clientes, Cuentas y Transacciones
-- =============================================

-- 1. Insertar Clientes Iniciales
INSERT INTO clients (identification_type, identification_number, first_name, last_name, email, birth_date, created_at, updated_at)
VALUES
    ('CC', 1001234567, 'Juan', 'Pérez García', 'juan.perez@email.com', '1990-05-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CC', 1007654321, 'María', 'López Torres', 'maria.lopez@email.com', '1985-08-22', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CE', 9001234567, 'Carlos', 'Rodríguez', 'carlos.rodriguez@email.com', '1992-03-10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CC', 1009876543, 'Ana', 'Martínez Ruiz', 'ana.martinez@email.com', '1988-12-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('PASSPORT', 123456789, 'Pedro', 'Sánchez Díaz', 'pedro.sanchez@email.com', '1995-07-18', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. Insertar Cuentas / Productos Financieros
-- Cuentas de Ahorro (prefijo 53)
INSERT INTO accounts (account_type, account_number, status, balance, gmf_exempt, created_at, updated_at, client_id, version)
VALUES
    ('SAVINGS', '5312345678', 'ACTIVE', 500000.00, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    ('SAVINGS', '5398765432', 'ACTIVE', 1200000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 0),
    ('SAVINGS', '5356781234', 'ACTIVE', 0.00, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 0);

-- Cuentas Corrientes (prefijo 33)
INSERT INTO accounts (account_type, account_number, status, balance, gmf_exempt, created_at, updated_at, client_id, version)
VALUES
    ('CHECKING', '3312345678', 'ACTIVE', 2000000.00, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    ('CHECKING', '3398765432', 'INACTIVE', 750000.00, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, 0);

-- 3. Insertar Transacciones de Prueba
-- Consignaciones
INSERT INTO transactions (transaction_type, amount, description, source_account_id, target_account_id, created_at)
VALUES
    ('DEPOSIT', 500000.00, 'Consignación inicial en efectivo', NULL, 1, CURRENT_TIMESTAMP),
    ('DEPOSIT', 1200000.00, 'Consignación de nómina', NULL, 2, CURRENT_TIMESTAMP),
    ('DEPOSIT', 2000000.00, 'Consignación apertura corriente', NULL, 4, CURRENT_TIMESTAMP);

-- Retiros
INSERT INTO transactions (transaction_type, amount, description, source_account_id, target_account_id, created_at)
VALUES
    ('WITHDRAWAL', 100000.00, 'Retiro en cajero electrónico', 1, NULL, CURRENT_TIMESTAMP),
    ('WITHDRAWAL', 200000.00, 'Retiro por ventanilla', 2, NULL, CURRENT_TIMESTAMP);

-- Transferencias entre cuentas
INSERT INTO transactions (transaction_type, amount, description, source_account_id, target_account_id, created_at)
VALUES
    ('TRANSFER', 150000.00, 'Transferencia a cuenta propia', 1, 4, CURRENT_TIMESTAMP),
    ('TRANSFER', 250000.00, 'Pago de transferencia a terceros', 4, 2, CURRENT_TIMESTAMP);
