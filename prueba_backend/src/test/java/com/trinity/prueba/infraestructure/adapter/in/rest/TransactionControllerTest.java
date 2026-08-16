package com.trinity.prueba.infraestructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trinity.prueba.application.dto.request.CreateTransactionRequest;
import com.trinity.prueba.domain.model.Transaction;
import com.trinity.prueba.domain.model.enums.TransactionType;
import com.trinity.prueba.domain.port.in.TransactionServicePort;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionServicePort transactionServicePort;

    @InjectMocks
    private TransactionController transactionController;

    private ObjectMapper objectMapper;
    private Transaction sampleTransaction;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        objectMapper = new ObjectMapper();

        sampleTransaction = new Transaction();
        sampleTransaction.setId(1L);
        sampleTransaction.setTransactionType(TransactionType.DEPOSIT);
        sampleTransaction.setAmount(new BigDecimal("500000.00"));
        sampleTransaction.setDescription("Consignacion inicial");
        sampleTransaction.setTargetAccountId(1L);
        sampleTransaction.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /api/v1/transactions debe retornar status 201 Created")
    void createTransaction_returnsCreated() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                TransactionType.DEPOSIT, new BigDecimal("500000.00"),
                "Consignacion inicial", null, 1L
        );

        when(transactionServicePort.createTransaction(any(Transaction.class))).thenReturn(sampleTransaction);

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.transactionType").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(500000.00));
    }

    @Test
    @DisplayName("GET /api/v1/transactions/account/{accountId} debe retornar status 200 OK")
    void getTransactionsByAccount_returnsOk() throws Exception {
        when(transactionServicePort.getTransactionsByAccountId(1L)).thenReturn(List.of(sampleTransaction));

        mockMvc.perform(get("/api/v1/transactions/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].transactionType").value("DEPOSIT"));
    }

    @Test
    @DisplayName("GET /api/v1/transactions/account/{accountId}/statement debe retornar status 200 OK")
    void getAccountStatement_returnsOk() throws Exception {
        when(transactionServicePort.getAccountStatement(1L)).thenReturn(List.of(sampleTransaction));

        mockMvc.perform(get("/api/v1/transactions/account/1/statement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
