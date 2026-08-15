package com.trinity.prueba.application.dto.request;

import com.trinity.prueba.domain.model.enums.AccountType;

public class CreateAccountRequest {

    private AccountType accountType;
    private Long clientId;
    private boolean gmfExempt;

    public CreateAccountRequest() {
    }

    public CreateAccountRequest(AccountType accountType, Long clientId, boolean gmfExempt) {
        this.accountType = accountType;
        this.clientId = clientId;
        this.gmfExempt = gmfExempt;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public boolean isGmfExempt() {
        return gmfExempt;
    }

    public void setGmfExempt(boolean gmfExempt) {
        this.gmfExempt = gmfExempt;
    }
}
