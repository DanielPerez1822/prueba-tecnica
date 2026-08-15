package com.trinity.prueba.application.service;

import com.trinity.prueba.domain.model.Account;
import com.trinity.prueba.domain.model.AccountNumberFactory;
import com.trinity.prueba.domain.model.enums.AccountStatus;
import com.trinity.prueba.domain.model.enums.AccountType;
import com.trinity.prueba.domain.model.exception.AccountNotFoundException;
import com.trinity.prueba.domain.model.exception.ClientNotFoundException;
import com.trinity.prueba.domain.port.in.AccountServicePort;
import com.trinity.prueba.domain.port.out.AccountRepositoryPort;
import com.trinity.prueba.domain.port.out.ClientRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public class AccountService implements AccountServicePort {

    private final AccountRepositoryPort accountRepository;
    private final ClientRepositoryPort clientRepository;

    public AccountService(AccountRepositoryPort accountRepository,
                          ClientRepositoryPort clientRepository) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional
    public Account createAccount(Account account) {
        // RN-P02: Verificar que el cliente existe
        clientRepository.findById(account.getClientId())
            .orElseThrow(() -> new ClientNotFoundException(
                "Cliente no encontrado con ID: " + account.getClientId()));

        // RN-P05 + RN-P06: Generar número de cuenta único de 10 dígitos
        String accountNumber;
        do {
            accountNumber = AccountNumberFactory.generate(account.getAccountType());
        } while (accountRepository.existsByAccountNumber(accountNumber));
        account.setAccountNumber(accountNumber);

        // RN-P07: Estado por defecto ACTIVA
        account.setStatus(AccountStatus.ACTIVE);

        // Saldo inicial en $0 si no se especifica
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }

        // RN-P09: Fecha de creación automática
        LocalDateTime now = LocalDateTime.now();
        account.setCreatedAt(now);
        account.setUpdatedAt(now);

        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account updateAccountStatus(Long id, AccountStatus newStatus) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new AccountNotFoundException(
                "Cuenta no encontrada con ID: " + id));

        // RN-P04 / RN-P08: Aplicar cambio de estado con validaciones de dominio
        switch (newStatus) {
            case ACTIVE -> account.activate();
            case INACTIVE -> account.inactivate();
            case CANCELLED -> account.cancel();
        }

        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public void cancelAccount(Long id) {
        updateAccountStatus(id, AccountStatus.CANCELLED);
    }

    @Override
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public List<Account> getAccountsByClientId(Long clientId) {
        return accountRepository.findByClientId(clientId);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}
