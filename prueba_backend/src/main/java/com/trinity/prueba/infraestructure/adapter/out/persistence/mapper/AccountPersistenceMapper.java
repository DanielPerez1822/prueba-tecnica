package com.trinity.prueba.infraestructure.adapter.out.persistence.mapper;

import com.trinity.prueba.domain.model.Account;
import com.trinity.prueba.infraestructure.adapter.out.persistence.entity.AccountEntity;
import org.springframework.stereotype.Component;

@Component
public class AccountPersistenceMapper {

    public AccountEntity toEntity(Account domain) {
        if (domain == null) {
            return null;
        }
        return new AccountEntity(
                domain.getId(),
                domain.getAccountType(),
                domain.getAccountNumber(),
                domain.getStatus(),
                domain.getBalance(),
                domain.isGmfExempt(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getClientId()
        );
    }

    public Account toDomain(AccountEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Account(
                entity.getId(),
                entity.getAccountType(),
                entity.getAccountNumber(),
                entity.getStatus(),
                entity.getBalance(),
                entity.isGmfExempt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getClientId()
        );
    }

    public void updateEntity(Account domain, AccountEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.setAccountType(domain.getAccountType());
        entity.setAccountNumber(domain.getAccountNumber());
        entity.setStatus(domain.getStatus());
        entity.setBalance(domain.getBalance());
        entity.setGmfExempt(domain.isGmfExempt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setClientId(domain.getClientId());
    }
}
