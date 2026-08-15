package com.trinity.prueba.domain.model.enums;

public enum AccountStatus {
    ACTIVE("Activa"),
    INACTIVE("Inactiva"),
    CANCELLED("Cancelada");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
