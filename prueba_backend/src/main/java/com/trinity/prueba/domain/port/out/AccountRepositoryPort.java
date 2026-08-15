package com.trinity.prueba.domain.port.out;

import com.trinity.prueba.domain.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepositoryPort {

    Account save(Account account);

    Optional<Account> findById(Long id);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByClientId(Long clientId);

    List<Account> findAll();

    boolean existsByClientId(Long clientId);

    boolean existsByAccountNumber(String accountNumber);
}
