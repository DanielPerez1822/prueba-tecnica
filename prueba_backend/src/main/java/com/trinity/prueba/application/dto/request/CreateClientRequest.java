package com.trinity.prueba.application.dto.request;

import com.trinity.prueba.domain.model.enums.IdentificationType;

import java.time.LocalDate;

public class CreateClientRequest {

    private IdentificationType identificationType;
    private Long identificationNumber;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;

    public CreateClientRequest() {
    }

    public CreateClientRequest(IdentificationType identificationType, Long identificationNumber,
                               String firstName, String lastName, String email, LocalDate birthDate) {
        this.identificationType = identificationType;
        this.identificationNumber = identificationNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
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
}
