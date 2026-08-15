# 📝 DML — Manipulación de Datos (Data Manipulation Language)

## 1. Descripción

Los scripts **DML** permiten **insertar**, **consultar**, **actualizar** y **eliminar** datos en las tablas de la base de datos. Incluye datos de prueba y consultas útiles para la operación del sistema.

---

## 2. Datos de Prueba (Seeds)

### Insertar Clientes

```sql
-- =============================================
-- INSERTAR CLIENTES DE PRUEBA
-- =============================================
INSERT INTO clients (identification_type, identification_number, first_name, last_name, email, birth_date, created_at, updated_at)
VALUES
    ('CC', '1001234567', 'Juan', 'Pérez García', 'juan.perez@email.com', '1990-05-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CC', '1007654321', 'María', 'López Torres', 'maria.lopez@email.com', '1985-08-22', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CE', '9001234567', 'Carlos', 'Rodríguez', 'carlos.rodriguez@email.com', '1992-03-10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CC', '1009876543', 'Ana', 'Martínez Ruiz', 'ana.martinez@email.com', '1988-12-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('PASSPORT', 'P12345678', 'Pedro', 'Sánchez Díaz', 'pedro.sanchez@email.com', '1995-07-18', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

### Insertar Cuentas (Productos)

```sql
-- =============================================
-- INSERTAR CUENTAS DE PRUEBA
-- =============================================
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
```

### Insertar Transacciones

```sql
-- =============================================
-- INSERTAR TRANSACCIONES DE PRUEBA
-- =============================================
-- Consignaciones
INSERT INTO transactions (transaction_type, amount, description, source_account_id, target_account_id, created_at)
VALUES
    ('DEPOSIT', 500000.00, 'Consignación inicial', NULL, 1, CURRENT_TIMESTAMP),
    ('DEPOSIT', 1200000.00, 'Consignación nómina', NULL, 2, CURRENT_TIMESTAMP),
    ('DEPOSIT', 300000.00, 'Consignación efectivo', NULL, 4, CURRENT_TIMESTAMP);

-- Retiros
INSERT INTO transactions (transaction_type, amount, description, source_account_id, target_account_id, created_at)
VALUES
    ('WITHDRAWAL', 100000.00, 'Retiro cajero ATM', 1, NULL, CURRENT_TIMESTAMP),
    ('WITHDRAWAL', 200000.00, 'Retiro en ventanilla', 2, NULL, CURRENT_TIMESTAMP);

-- Transferencias
INSERT INTO transactions (transaction_type, amount, description, source_account_id, target_account_id, created_at)
VALUES
    ('TRANSFER', 150000.00, 'Transferencia entre cuentas propias', 1, 4, CURRENT_TIMESTAMP),
    ('TRANSFER', 250000.00, 'Pago a tercero', 4, 2, CURRENT_TIMESTAMP);
```

---

## 3. Consultas de Operación

### Consultas de Clientes

```sql
-- Obtener todos los clientes
SELECT id, identification_type, identification_number,
       first_name, last_name, email, birth_date,
       created_at, updated_at
FROM clients
ORDER BY created_at DESC;

-- Buscar cliente por número de identificación
SELECT * FROM clients
WHERE identification_number = '1001234567';

-- Buscar clientes por nombre
SELECT * FROM clients
WHERE first_name ILIKE '%juan%' OR last_name ILIKE '%juan%';

-- Clientes con sus cuentas
SELECT c.id, c.first_name, c.last_name,
       COUNT(a.id) AS total_cuentas,
       COALESCE(SUM(a.balance), 0) AS saldo_total
FROM clients c
LEFT JOIN accounts a ON c.id = a.client_id
GROUP BY c.id, c.first_name, c.last_name
ORDER BY saldo_total DESC;

-- Clientes sin cuentas (candidatos a eliminar)
SELECT c.*
FROM clients c
LEFT JOIN accounts a ON c.id = a.client_id
WHERE a.id IS NULL;
```

### Consultas de Cuentas

```sql
-- Obtener todas las cuentas con datos del cliente
SELECT a.id, a.account_type, a.account_number, a.status, a.balance,
       a.gmf_exempt, a.created_at,
       c.first_name || ' ' || c.last_name AS cliente
FROM accounts a
JOIN clients c ON a.client_id = c.id
ORDER BY a.created_at DESC;

-- Cuentas por cliente
SELECT a.* FROM accounts a
WHERE a.client_id = 1;

-- Cuentas activas
SELECT * FROM accounts WHERE status = 'ACTIVE';

-- Cuentas con saldo cero (candidatas a cancelación)
SELECT a.*, c.first_name || ' ' || c.last_name AS cliente
FROM accounts a
JOIN clients c ON a.client_id = c.id
WHERE a.balance = 0 AND a.status != 'CANCELLED';

-- Saldo total por tipo de cuenta
SELECT account_type,
       COUNT(*) AS total_cuentas,
       SUM(balance) AS saldo_total,
       AVG(balance) AS saldo_promedio,
       MIN(balance) AS saldo_minimo,
       MAX(balance) AS saldo_maximo
FROM accounts
WHERE status = 'ACTIVE'
GROUP BY account_type;
```

### Consultas de Transacciones

```sql
-- Estado de cuenta de una cuenta específica
SELECT t.id, t.transaction_type, t.amount, t.description, t.created_at,
       CASE
           WHEN t.target_account_id = 1 THEN 'CRÉDITO'
           WHEN t.source_account_id = 1 THEN 'DÉBITO'
       END AS tipo_movimiento
FROM transactions t
WHERE t.source_account_id = 1 OR t.target_account_id = 1
ORDER BY t.created_at DESC;

-- Resumen de transacciones por tipo
SELECT transaction_type,
       COUNT(*) AS total,
       SUM(amount) AS monto_total,
       AVG(amount) AS monto_promedio
FROM transactions
GROUP BY transaction_type;

-- Transacciones del día
SELECT * FROM transactions
WHERE DATE(created_at) = CURRENT_DATE
ORDER BY created_at DESC;

-- Top 10 transacciones más grandes
SELECT t.*, 
       sa.account_number AS cuenta_origen,
       ta.account_number AS cuenta_destino
FROM transactions t
LEFT JOIN accounts sa ON t.source_account_id = sa.id
LEFT JOIN accounts ta ON t.target_account_id = ta.id
ORDER BY t.amount DESC
LIMIT 10;

-- Movimientos entre dos fechas
SELECT * FROM transactions
WHERE created_at BETWEEN '2026-01-01' AND '2026-12-31'
ORDER BY created_at;
```

---

## 4. Operaciones de Actualización

```sql
-- Actualizar datos de un cliente
UPDATE clients
SET first_name = 'Juan Carlos',
    last_name = 'Pérez García',
    email = 'juancarlos.perez@email.com',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1;

-- Cambiar estado de una cuenta
UPDATE accounts
SET status = 'INACTIVE',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1;

-- Cancelar cuenta con saldo cero
UPDATE accounts
SET status = 'CANCELLED',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 3 AND balance = 0;

-- Actualizar saldo de cuenta (por transacción)
-- Consignación: sumar al saldo
UPDATE accounts
SET balance = balance + 100000.00,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1;

-- Retiro: restar del saldo
UPDATE accounts
SET balance = balance - 50000.00,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1 AND balance >= 50000.00;  -- Validar saldo suficiente
```

---

## 5. Operaciones de Eliminación

```sql
-- Eliminar un cliente (solo si no tiene cuentas)
DELETE FROM clients
WHERE id = 5
AND NOT EXISTS (
    SELECT 1 FROM accounts WHERE client_id = 5
);

-- Eliminar transacciones de prueba (solo en desarrollo)
-- ⚠️ CUIDADO: No ejecutar en producción
-- DELETE FROM transactions WHERE description LIKE '%prueba%';
```

---

## 6. Consultas Avanzadas

### Reporte de Estado de Cuenta Completo

```sql
-- Estado de cuenta detallado con saldo acumulado
WITH movimientos AS (
    SELECT
        t.created_at,
        t.transaction_type,
        t.description,
        CASE
            WHEN t.target_account_id = :account_id THEN t.amount
            ELSE 0
        END AS credito,
        CASE
            WHEN t.source_account_id = :account_id THEN t.amount
            ELSE 0
        END AS debito
    FROM transactions t
    WHERE t.source_account_id = :account_id
       OR t.target_account_id = :account_id
    ORDER BY t.created_at
)
SELECT
    created_at AS fecha,
    transaction_type AS tipo,
    description AS descripcion,
    credito,
    debito,
    SUM(credito - debito) OVER (ORDER BY created_at) AS saldo_acumulado
FROM movimientos;
```

### Reporte de Clientes con Mayor Saldo

```sql
SELECT
    c.id,
    c.first_name || ' ' || c.last_name AS nombre_completo,
    c.identification_number,
    COUNT(a.id) AS total_cuentas,
    SUM(CASE WHEN a.account_type = 'SAVINGS' THEN a.balance ELSE 0 END) AS saldo_ahorros,
    SUM(CASE WHEN a.account_type = 'CHECKING' THEN a.balance ELSE 0 END) AS saldo_corriente,
    SUM(a.balance) AS saldo_total
FROM clients c
JOIN accounts a ON c.id = a.client_id
WHERE a.status = 'ACTIVE'
GROUP BY c.id, c.first_name, c.last_name, c.identification_number
ORDER BY saldo_total DESC;
```

---

## 7. Script de Limpieza (Solo Desarrollo)

```sql
-- =============================================
-- ⚠️ SCRIPT DE LIMPIEZA - SOLO PARA DESARROLLO
-- Elimina todos los datos respetando el orden de FK
-- =============================================

-- 1. Eliminar transacciones primero (depende de accounts)
TRUNCATE TABLE transactions CASCADE;

-- 2. Eliminar cuentas (depende de clients)
TRUNCATE TABLE accounts CASCADE;

-- 3. Eliminar clientes
TRUNCATE TABLE clients CASCADE;

-- Resetear secuencias
ALTER SEQUENCE clients_id_seq RESTART WITH 1;
ALTER SEQUENCE accounts_id_seq RESTART WITH 1;
ALTER SEQUENCE transactions_id_seq RESTART WITH 1;
```

---

## 8. Resumen de Operaciones DML

| Operación | Tabla | Descripción |
|-----------|-------|-------------|
| `INSERT` | `clients` | Registrar nuevos clientes |
| `INSERT` | `accounts` | Crear cuentas bancarias |
| `INSERT` | `transactions` | Registrar movimientos financieros |
| `SELECT` | `clients` | Consultar clientes y sus datos |
| `SELECT` | `accounts` | Consultar cuentas y saldos |
| `SELECT` | `transactions` | Consultar estado de cuenta |
| `UPDATE` | `clients` | Modificar datos del cliente |
| `UPDATE` | `accounts` | Cambiar estado o actualizar saldo |
| `DELETE` | `clients` | Eliminar cliente (sin productos) |
