package com.trinity.prueba.domain.port.in;

import com.trinity.prueba.domain.model.Account;
import com.trinity.prueba.domain.model.enums.AccountStatus;

import java.util.List;
import java.util.Optional;

public interface AccountServicePort {

    Account createAccount(Account account);

    Account updateAccountStatus(Long id, AccountStatus status);

    void cancelAccount(Long id);

    Optional<Account> getAccountById(Long id);

    List<Account> getAccountsByClientId(Long clientId);

    List<Account> getAllAccounts();
}
