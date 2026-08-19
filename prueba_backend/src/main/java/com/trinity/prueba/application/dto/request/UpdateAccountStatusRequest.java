package com.trinity.prueba.application.dto.request;

import com.trinity.prueba.domain.model.enums.AccountStatus;

public class UpdateAccountStatusRequest {

    private AccountStatus status;

    public UpdateAccountStatusRequest() {
    }

    public UpdateAccountStatusRequest(AccountStatus status) {
        this.status = status;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
