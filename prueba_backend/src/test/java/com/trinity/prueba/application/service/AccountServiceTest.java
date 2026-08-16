package com.trinity.prueba.application.service;

import com.trinity.prueba.domain.model.Account;
import com.trinity.prueba.domain.model.Client;
import com.trinity.prueba.domain.model.enums.AccountStatus;
import com.trinity.prueba.domain.model.enums.AccountType;
import com.trinity.prueba.domain.model.exception.AccountNotFoundException;
import com.trinity.prueba.domain.model.exception.ClientNotFoundException;
import com.trinity.prueba.domain.model.exception.InvalidAccountStateException;
import com.trinity.prueba.domain.port.out.AccountRepositoryPort;
import com.trinity.prueba.domain.port.out.ClientRepositoryPort;
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
class AccountServiceTest {

    @Mock
    private AccountRepositoryPort accountRepository;

    @Mock
    private ClientRepositoryPort clientRepository;

    @InjectMocks
    private AccountService accountService;

    private Account validAccount;

    @BeforeEach
    void setUp() {
        validAccount = new Account();
        validAccount.setId(1L);
        validAccount.setAccountType(AccountType.SAVINGS);
        validAccount.setClientId(1L);
        validAccount.setStatus(AccountStatus.ACTIVE);
        validAccount.setBalance(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Crear cuenta de ahorros exitosamente con estado ACTIVE y prefijo 53")
    void createAccount_savings_success() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account created = accountService.createAccount(validAccount);

        assertNotNull(created);
        assertEquals(AccountStatus.ACTIVE, created.getStatus());
        assertTrue(created.getAccountNumber().startsWith("53"));
        assertEquals(10, created.getAccountNumber().length());
        assertNotNull(created.getCreatedAt());
        verify(accountRepository, times(1)).save(validAccount);
    }

    @Test
    @DisplayName("Crear cuenta corriente exitosamente con prefijo 33")
    void createAccount_checking_success() {
        validAccount.setAccountType(AccountType.CHECKING);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account created = accountService.createAccount(validAccount);

        assertNotNull(created);
        assertTrue(created.getAccountNumber().startsWith("33"));
        assertEquals(10, created.getAccountNumber().length());
    }

    @Test
    @DisplayName("Lanzar excepción al crear cuenta para un cliente no existente")
    void createAccount_clientNotFound_throwsClientNotFoundException() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());
        validAccount.setClientId(99L);

        assertThrows(ClientNotFoundException.class, () -> accountService.createAccount(validAccount));
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cambiar estado de cuenta a INACTIVE exitosamente")
    void updateAccountStatus_inactivate_success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(validAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account updated = accountService.updateAccountStatus(1L, AccountStatus.INACTIVE);

        assertEquals(AccountStatus.INACTIVE, updated.getStatus());
        verify(accountRepository, times(1)).save(validAccount);
    }

    @Test
    @DisplayName("Cancelar cuenta con saldo igual a $0 exitosamente")
    void updateAccountStatus_cancel_zeroBalance_success() {
        validAccount.setBalance(BigDecimal.ZERO);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(validAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account updated = accountService.updateAccountStatus(1L, AccountStatus.CANCELLED);

        assertEquals(AccountStatus.CANCELLED, updated.getStatus());
    }

    @Test
    @DisplayName("Lanzar excepción al intentar cancelar cuenta con saldo diferente a $0")
    void updateAccountStatus_cancel_nonZeroBalance_throwsInvalidAccountStateException() {
        validAccount.setBalance(new BigDecimal("50000.00"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(validAccount));

        assertThrows(InvalidAccountStateException.class, () ->
            accountService.updateAccountStatus(1L, AccountStatus.CANCELLED));

        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lanzar excepción al actualizar estado de cuenta no encontrada")
    void updateAccountStatus_notFound_throwsAccountNotFoundException() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () ->
            accountService.updateAccountStatus(99L, AccountStatus.ACTIVE));
    }

    @Test
    @DisplayName("Obtener cuenta por ID")
    void getAccountById_success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(validAccount));

        Optional<Account> result = accountService.getAccountById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    @DisplayName("Obtener cuentas por ID de cliente")
    void getAccountsByClientId_success() {
        when(accountRepository.findByClientId(1L)).thenReturn(List.of(validAccount));

        List<Account> result = accountService.getAccountsByClientId(1L);

        assertEquals(1, result.size());
    }
}
