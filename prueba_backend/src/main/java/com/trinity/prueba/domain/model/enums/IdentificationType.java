package com.trinity.prueba.domain.model.enums;

public enum IdentificationType {
    CC("Cédula de Ciudadanía"),
    CE("Cédula de Extranjería"),
    PASSPORT("Pasaporte");

    private final String description;

    IdentificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
