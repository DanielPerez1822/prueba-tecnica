package com.trinity.prueba.domain.model;

import com.trinity.prueba.domain.model.enums.IdentificationType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class Client {

    private Long id;
    private IdentificationType identificationType;
    private Long identificationNumber;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Client() {
    }

    public Client(Long id, IdentificationType identificationType, Long identificationNumber,
                  String firstName, String lastName, String email, LocalDate birthDate,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.identificationType = identificationType;
        this.identificationNumber = identificationNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ==========================================
    // REGLAS DE NEGOCIO (Domain Logic)
    // ==========================================

    /**
     * RN-01: Verifica si el cliente es menor de edad.
     * Un cliente debe tener al menos 18 años.
     */
    public boolean isUnderage() {
        if (this.birthDate == null) {
            return true;
        }
        return Period.between(this.birthDate, LocalDate.now()).getYears() < 18;
    }

    /**
     * RN-05: Valida el formato del correo electrónico (xxxx@xxxxx.xxx).
     */
    public boolean hasValidEmail() {
        if (this.email == null || this.email.isBlank()) {
            return false;
        }
        return this.email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * RN-06: Valida que el nombre y apellido tengan al menos 2 caracteres.
     */
    public boolean hasValidName() {
        return this.firstName != null && this.firstName.trim().length() >= 2
            && this.lastName != null && this.lastName.trim().length() >= 2;
    }

    /**
     * Valida que el número de identificación sea un valor numérico positivo válido.
     */
    public boolean hasValidIdentificationNumber() {
        return this.identificationNumber != null && this.identificationNumber > 0;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IdentificationType getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(IdentificationType identificationType) {
        this.identificationType = identificationType;
    }

    public Long getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(Long identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
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
}
