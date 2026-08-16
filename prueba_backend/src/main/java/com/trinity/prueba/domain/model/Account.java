package com.trinity.prueba.domain.model;

import com.trinity.prueba.domain.model.enums.AccountStatus;
import com.trinity.prueba.domain.model.enums.AccountType;
import com.trinity.prueba.domain.model.exception.InsufficientBalanceException;
import com.trinity.prueba.domain.model.exception.InvalidAccountStateException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class Account {

    private Long id;
    private AccountType accountType;
    private String accountNumber;
    private AccountStatus status;
    private BigDecimal balance;
    private boolean gmfExempt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long clientId;

    public Account() {
    }

    public Account(Long id, AccountType accountType, String accountNumber, AccountStatus status,
                   BigDecimal balance, boolean gmfExempt, LocalDateTime createdAt,
                   LocalDateTime updatedAt, Long clientId) {
        this.id = id;
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.status = status;
        this.balance = balance;
        this.gmfExempt = gmfExempt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.clientId = clientId;
    }

    // ==========================================
    // REGLAS DE NEGOCIO (Domain Logic)
    // ==========================================

    /**
     * RN-P10: Acredita (suma) un monto a la cuenta.
     */
    public void credit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a $0");
        }
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * RN-P03 + RN-P10: Debita (resta) un monto de la cuenta.
     * Cuentas de ahorro no pueden quedar con saldo negativo.
     */
    public void debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a $0");
        }
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }
        BigDecimal newBalance = this.balance.subtract(amount);
        if (this.accountType == AccountType.SAVINGS && newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException(
                "La cuenta de ahorros no puede tener saldo menor a $0. Saldo actual: $" +
                this.balance + ", monto a debitar: $" + amount);
        }
        this.balance = newBalance;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calcula la tasa del GMF (4x1000 = 0.4%) sobre el monto especificado.
     * Retorna 0 si la cuenta es exenta de GMF (gmfExempt == true).
     */
    public BigDecimal calculateGmf(BigDecimal amount) {
        if (this.gmfExempt || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(new BigDecimal("0.004")).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Debita el monto especificado más el recargo de GMF (4x1000) correspondiente.
     * @return El valor del GMF calculado y debitado.
     */
    public BigDecimal debitWithGmf(BigDecimal amount) {
        BigDecimal gmf = calculateGmf(amount);
        BigDecimal totalDebit = amount.add(gmf);
        debit(totalDebit);
        return gmf;
    }

    /**
     * RN-P04: Activa la cuenta.
     */
    public void activate() {
        if (this.status == AccountStatus.CANCELLED) {
            throw new InvalidAccountStateException("No se puede activar una cuenta cancelada");
        }
        this.status = AccountStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * RN-P04: Inactiva la cuenta.
     */
    public void inactivate() {
        if (this.status == AccountStatus.CANCELLED) {
            throw new InvalidAccountStateException("No se puede inactivar una cuenta cancelada");
        }
        this.status = AccountStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * RN-P08: Cancela la cuenta. Solo si saldo = $0.
     */
    public void cancel() {
        if (this.balance == null || this.balance.compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidAccountStateException(
                "Solo se pueden cancelar cuentas con saldo igual a $0. Saldo actual: $" + this.balance);
        }
        this.status = AccountStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean isGmfExempt() {
        return gmfExempt;
    }

    public void setGmfExempt(boolean gmfExempt) {
        this.gmfExempt = gmfExempt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
