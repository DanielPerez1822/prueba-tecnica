package com.trinity.prueba.application.dto.request;

import com.trinity.prueba.domain.model.enums.TransactionType;

import java.math.BigDecimal;

public class CreateTransactionRequest {

    private TransactionType transactionType;
    private BigDecimal amount;
    private String description;
    private Long sourceAccountId;
    private Long targetAccountId;

    public CreateTransactionRequest() {
    }

    public CreateTransactionRequest(TransactionType transactionType, BigDecimal amount, String description,
                                  Long sourceAccountId, Long targetAccountId) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
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
}
