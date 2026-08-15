package com.trinity.prueba.infraestructure.adapter.out.persistence.adapter;

import com.trinity.prueba.domain.model.Account;
import com.trinity.prueba.domain.port.out.AccountRepositoryPort;
import com.trinity.prueba.infraestructure.adapter.out.persistence.entity.AccountEntity;
import com.trinity.prueba.infraestructure.adapter.out.persistence.mapper.AccountPersistenceMapper;
import com.trinity.prueba.infraestructure.adapter.out.persistence.repository.JpaAccountRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AccountPersistenceAdapter implements AccountRepositoryPort {

    private final JpaAccountRepository jpaAccountRepository;
    private final AccountPersistenceMapper mapper;

    public AccountPersistenceAdapter(JpaAccountRepository jpaAccountRepository, AccountPersistenceMapper mapper) {
        this.jpaAccountRepository = jpaAccountRepository;
        this.mapper = mapper;
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity;
        if (account.getId() != null) {
            entity = jpaAccountRepository.findById(account.getId())
                    .orElseGet(() -> mapper.toEntity(account));
            mapper.updateEntity(account, entity);
        } else {
            entity = mapper.toEntity(account);
        }
        AccountEntity savedEntity = jpaAccountRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findById(Long id) {
        return jpaAccountRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return jpaAccountRepository.findByAccountNumber(accountNumber).map(mapper::toDomain);
    }

    @Override
    public List<Account> findByClientId(Long clientId) {
        return jpaAccountRepository.findByClientId(clientId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Account> findAll() {
        return jpaAccountRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByClientId(Long clientId) {
        return jpaAccountRepository.existsByClientId(clientId);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return jpaAccountRepository.existsByAccountNumber(accountNumber);
    }
}
