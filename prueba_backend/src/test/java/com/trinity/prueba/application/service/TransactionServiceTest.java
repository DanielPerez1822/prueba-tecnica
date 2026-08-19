package com.trinity.prueba.application.service;

import com.trinity.prueba.domain.model.Account;
import com.trinity.prueba.domain.model.Transaction;
import com.trinity.prueba.domain.model.enums.AccountStatus;
import com.trinity.prueba.domain.model.enums.AccountType;
import com.trinity.prueba.domain.model.enums.TransactionType;
import com.trinity.prueba.domain.model.exception.AccountNotFoundException;
import com.trinity.prueba.domain.model.exception.InsufficientBalanceException;
import com.trinity.prueba.domain.model.exception.InvalidAccountStateException;
import com.trinity.prueba.domain.model.exception.InvalidTransactionException;
import com.trinity.prueba.domain.port.out.AccountRepositoryPort;
import com.trinity.prueba.domain.port.out.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @Mock
    private AccountRepositoryPort accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account sourceAccount;
    private Account targetAccount;

    @BeforeEach
    void setUp() {
        sourceAccount = new Account();
        sourceAccount.setId(1L);
        sourceAccount.setAccountNumber("5312345678");
        sourceAccount.setAccountType(AccountType.SAVINGS);
        sourceAccount.setStatus(AccountStatus.ACTIVE);
        sourceAccount.setBalance(new BigDecimal("500000.00"));
        sourceAccount.setGmfExempt(true);

        targetAccount = new Account();
        targetAccount.setId(2L);
        targetAccount.setAccountNumber("5398765432");
        targetAccount.setAccountType(AccountType.SAVINGS);
        targetAccount.setStatus(AccountStatus.ACTIVE);
        targetAccount.setBalance(new BigDecimal("100000.00"));
    }

    @Test
    @DisplayName("Ejecutar consignación (DEPOSIT) exitosamente incrementando el saldo")
    void createTransaction_deposit_success() {
        Transaction deposit = new Transaction();
        deposit.setTransactionType(TransactionType.DEPOSIT);
        deposit.setAmount(new BigDecimal("200000.00"));
        deposit.setTargetAccountId(2L);

        when(accountRepository.findById(2L)).thenReturn(Optional.of(targetAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = transactionService.createTransaction(deposit);

        assertNotNull(result);
        assertEquals(new BigDecimal("300000.00"), targetAccount.getBalance());
        verify(accountRepository, times(1)).save(targetAccount);
        verify(transactionRepository, times(1)).save(deposit);
    }

    @Test
    @DisplayName("Ejecutar retiro (WITHDRAWAL) exitosamente disminuyendo el saldo")
    void createTransaction_withdrawal_success() {
        Transaction withdrawal = new Transaction();
        withdrawal.setTransactionType(TransactionType.WITHDRAWAL);
        withdrawal.setAmount(new BigDecimal("100000.00"));
        withdrawal.setSourceAccountId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = transactionService.createTransaction(withdrawal);

        assertNotNull(result);
        assertEquals(new BigDecimal("400000.00"), sourceAccount.getBalance());
        verify(accountRepository, times(1)).save(sourceAccount);
        verify(transactionRepository, times(1)).save(withdrawal);
    }

    @Test
    @DisplayName("Lanzar excepción al intentar retirar un monto mayor al saldo en cuenta de ahorros")
    void createTransaction_withdrawal_insufficientBalance_throwsInsufficientBalanceException() {
        Transaction withdrawal = new Transaction();
        withdrawal.setTransactionType(TransactionType.WITHDRAWAL);
        withdrawal.setAmount(new BigDecimal("600000.00"));
        withdrawal.setSourceAccountId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));

        assertThrows(InsufficientBalanceException.class, () ->
            transactionService.createTransaction(withdrawal));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ejecutar transferencia (TRANSFER) exitosamente debitando origen y acreditando destino")
    void createTransaction_transfer_success() {
        Transaction transfer = new Transaction();
        transfer.setTransactionType(TransactionType.TRANSFER);
        transfer.setAmount(new BigDecimal("200000.00"));
        transfer.setSourceAccountId(1L);
        transfer.setTargetAccountId(2L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(targetAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = transactionService.createTransaction(transfer);

        assertNotNull(result);
        assertEquals(new BigDecimal("300000.00"), sourceAccount.getBalance());
        assertEquals(new BigDecimal("300000.00"), targetAccount.getBalance());
        verify(accountRepository, times(1)).save(sourceAccount);
        verify(accountRepository, times(1)).save(targetAccount);
    }

    @Test
    @DisplayName("Lanzar excepción si se intenta transferir a la misma cuenta")
    void createTransaction_transfer_sameAccount_throwsInvalidTransactionException() {
        Transaction transfer = new Transaction();
        transfer.setTransactionType(TransactionType.TRANSFER);
        transfer.setAmount(new BigDecimal("100000.00"));
        transfer.setSourceAccountId(1L);
        transfer.setTargetAccountId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));

        assertThrows(InvalidTransactionException.class, () ->
            transactionService.createTransaction(transfer));
    }

    @Test
    @DisplayName("Lanzar excepción al realizar transacción con cuenta inactiva")
    void createTransaction_inactiveAccount_throwsInvalidAccountStateException() {
        sourceAccount.setStatus(AccountStatus.INACTIVE);
        Transaction withdrawal = new Transaction();
        withdrawal.setTransactionType(TransactionType.WITHDRAWAL);
        withdrawal.setAmount(new BigDecimal("50000.00"));
        withdrawal.setSourceAccountId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));

        assertThrows(InvalidAccountStateException.class, () ->
            transactionService.createTransaction(withdrawal));
    }

    @Test
    @DisplayName("Lanzar excepción al intentar transacción con monto <= 0")
    void createTransaction_invalidAmount_throwsInvalidTransactionException() {
        Transaction deposit = new Transaction();
        deposit.setTransactionType(TransactionType.DEPOSIT);
        deposit.setAmount(BigDecimal.ZERO);
        deposit.setTargetAccountId(1L);

        assertThrows(InvalidTransactionException.class, () ->
            transactionService.createTransaction(deposit));
    }

    @Test
    @DisplayName("Obtener estado de cuenta para una cuenta existente")
    void getAccountStatement_success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(transactionRepository.findByAccountId(1L)).thenReturn(List.of(new Transaction()));

        List<Transaction> statement = transactionService.getAccountStatement(1L);

        assertEquals(1, statement.size());
    }

    @Test
    @DisplayName("Lanzar excepción al solicitar estado de cuenta no existente")
    void getAccountStatement_notFound_throwsAccountNotFoundException() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () ->
            transactionService.getAccountStatement(99L));
    }
}
