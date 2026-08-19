package com.trinity.prueba.domain.model.enums;

public enum TransactionType {
    DEPOSIT("Consignación"),
    WITHDRAWAL("Retiro"),
    TRANSFER("Transferencia");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
