package com.trinity.prueba.infraestructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trinity.prueba.application.dto.request.CreateAccountRequest;
import com.trinity.prueba.application.dto.request.UpdateAccountStatusRequest;
import com.trinity.prueba.domain.model.Account;
import com.trinity.prueba.domain.model.enums.AccountStatus;
import com.trinity.prueba.domain.model.enums.AccountType;
import com.trinity.prueba.domain.port.in.AccountServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountServicePort accountServicePort;

    @InjectMocks
    private AccountController accountController;

    private ObjectMapper objectMapper;
    private Account sampleAccount;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        objectMapper = new ObjectMapper();

        sampleAccount = new Account();
        sampleAccount.setId(1L);
        sampleAccount.setAccountType(AccountType.SAVINGS);
        sampleAccount.setAccountNumber("5312345678");
        sampleAccount.setStatus(AccountStatus.ACTIVE);
        sampleAccount.setBalance(new BigDecimal("500000.00"));
        sampleAccount.setClientId(1L);
    }

    @Test
    @DisplayName("POST /api/v1/accounts debe retornar status 201 Created")
    void createAccount_returnsCreated() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest(AccountType.SAVINGS, 1L, false);

        when(accountServicePort.createAccount(any(Account.class))).thenReturn(sampleAccount);

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountNumber").value("5312345678"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /api/v1/accounts debe retornar status 200 OK con todas las cuentas")
    void getAllAccounts_returnsOk() throws Exception {
        when(accountServicePort.getAllAccounts()).thenReturn(List.of(sampleAccount));

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].accountNumber").value("5312345678"));
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{id} debe retornar status 200 OK cuando existe")
    void getAccountById_exists_returnsOk() throws Exception {
        when(accountServicePort.getAccountById(1L)).thenReturn(Optional.of(sampleAccount));

        mockMvc.perform(get("/api/v1/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountNumber").value("5312345678"));
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{id} debe retornar status 404 Not Found cuando no existe")
    void getAccountById_notFound_returns404() throws Exception {
        when(accountServicePort.getAccountById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/accounts/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/accounts/client/{clientId} debe retornar status 200 OK con cuentas del cliente")
    void getAccountsByClientId_returnsOk() throws Exception {
        when(accountServicePort.getAccountsByClientId(1L)).thenReturn(List.of(sampleAccount));

        mockMvc.perform(get("/api/v1/accounts/client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("PUT /api/v1/accounts/{id}/status debe retornar status 200 OK al cambiar estado")
    void updateAccountStatus_returnsOk() throws Exception {
        UpdateAccountStatusRequest request = new UpdateAccountStatusRequest(AccountStatus.INACTIVE);
        sampleAccount.setStatus(AccountStatus.INACTIVE);

        when(accountServicePort.updateAccountStatus(eq(1L), eq(AccountStatus.INACTIVE))).thenReturn(sampleAccount);

        mockMvc.perform(put("/api/v1/accounts/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{id} debe retornar status 200 OK al cancelar cuenta")
    void cancelAccount_returnsOk() throws Exception {
        doNothing().when(accountServicePort).cancelAccount(1L);

        mockMvc.perform(delete("/api/v1/accounts/1"))
                .andExpect(status().isOk());
    }
}
