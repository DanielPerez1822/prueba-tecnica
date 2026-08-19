package com.trinity.prueba.domain.model;

import com.trinity.prueba.domain.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private Long id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String description;
    private Long sourceAccountId;
    private Long targetAccountId;
    private LocalDateTime createdAt;

    public Transaction() {
    }

    public Transaction(Long id, TransactionType transactionType, BigDecimal amount, String description,
                       Long sourceAccountId, Long targetAccountId, LocalDateTime createdAt) {
        this.id = id;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.createdAt = createdAt;
    }

    // ==========================================
    // REGLAS DE NEGOCIO (Domain Methods)
    // ==========================================

    /**
     * Valida que el monto de la transacción sea positivo (> $0).
     */
    public boolean hasValidAmount() {
        return this.amount != null && this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * RN-T01: Valida que el tipo de transacción tenga las cuentas requeridas.
     */
    public boolean hasRequiredAccounts() {
        if (this.transactionType == null) {
            return false;
        }
        return switch (this.transactionType) {
            case DEPOSIT -> this.targetAccountId != null;
            case WITHDRAWAL -> this.sourceAccountId != null;
            case TRANSFER -> this.sourceAccountId != null && this.targetAccountId != null;
        };
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public Long getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(Long targetAccountId) {
        this.targetAccountId = targetAccountId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
