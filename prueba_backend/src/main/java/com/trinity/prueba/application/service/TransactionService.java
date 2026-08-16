package com.trinity.prueba.application.service;

import com.trinity.prueba.domain.model.Account;
import com.trinity.prueba.domain.model.Transaction;
import com.trinity.prueba.domain.model.enums.AccountStatus;
import com.trinity.prueba.domain.model.exception.AccountNotFoundException;
import com.trinity.prueba.domain.model.exception.InvalidAccountStateException;
import com.trinity.prueba.domain.model.exception.InvalidTransactionException;
import com.trinity.prueba.domain.port.in.TransactionServicePort;
import com.trinity.prueba.domain.port.out.AccountRepositoryPort;
import com.trinity.prueba.domain.port.out.TransactionRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public class TransactionService implements TransactionServicePort {

    private final TransactionRepositoryPort transactionRepository;
    private final AccountRepositoryPort accountRepository;

    public TransactionService(TransactionRepositoryPort transactionRepository,
                              AccountRepositoryPort accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        // Validar monto positivo
        if (!transaction.hasValidAmount()) {
            throw new InvalidTransactionException("El monto debe ser mayor a $0");
        }

        // Validar cuentas requeridas según tipo de transacción
        if (!transaction.hasRequiredAccounts()) {
            throw new InvalidTransactionException(
                "La transacción no tiene las cuentas requeridas para el tipo: "
                + transaction.getTransactionType());
        }

        // Ejecutar transacción según tipo
        return switch (transaction.getTransactionType()) {
            case DEPOSIT -> executeDeposit(transaction);
            case WITHDRAWAL -> executeWithdrawal(transaction);
            case TRANSFER -> executeTransfer(transaction);
        };
    }

    private Transaction executeDeposit(Transaction transaction) {
        // Obtener cuenta destino activa
        Account targetAccount = getActiveAccount(transaction.getTargetAccountId());

        // RN-T02: Acreditar monto
        targetAccount.credit(transaction.getAmount());
        accountRepository.save(targetAccount);

        // Registrar transacción
        transaction.setCreatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    private Transaction executeWithdrawal(Transaction transaction) {
        // Obtener cuenta origen activa
        Account sourceAccount = getActiveAccount(transaction.getSourceAccountId());

        // RN-T02 + RN-T05 + GMF: Debitar monto más GMF (4x1000) si la cuenta no es exenta
        sourceAccount.debitWithGmf(transaction.getAmount());
        accountRepository.save(sourceAccount);

        // Registrar transacción
        transaction.setCreatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    private Transaction executeTransfer(Transaction transaction) {
        // RN-T03 + RN-T06: Verificar que ambas cuentas existen y están activas
        Account sourceAccount = getActiveAccount(transaction.getSourceAccountId());
        Account targetAccount = getActiveAccount(transaction.getTargetAccountId());

        // Validar que no se transfiera a la misma cuenta
        if (sourceAccount.getId().equals(targetAccount.getId())) {
            throw new InvalidTransactionException("No se puede transferir a la misma cuenta");
        }

        // RN-T04 + GMF: Débito en cuenta origen con recargo de GMF (4x1000) si no es exenta
        sourceAccount.debitWithGmf(transaction.getAmount());
        accountRepository.save(sourceAccount);

        // RN-T04: Crédito del monto bruto en cuenta destino
        targetAccount.credit(transaction.getAmount());
        accountRepository.save(targetAccount);

        // Registrar la transacción
        transaction.setCreatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    private Account getActiveAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con ID: " + accountId));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountStateException(
                "La cuenta " + account.getAccountNumber() + " no está activa. Estado actual: " + account.getStatus());
        }

        return account;
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @Override
    public List<Transaction> getAccountStatement(Long accountId) {
        // Verificar que la cuenta existe
        accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con ID: " + accountId));

        return transactionRepository.findByAccountId(accountId);
    }
}
