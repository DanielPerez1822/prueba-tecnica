package com.trinity.prueba.infraestructure.adapter.in.rest;

import com.trinity.prueba.application.dto.request.CreateTransactionRequest;
import com.trinity.prueba.application.dto.response.TransactionResponse;
import com.trinity.prueba.domain.model.Transaction;
import com.trinity.prueba.domain.port.in.TransactionServicePort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@CrossOrigin(origins = "http://localhost:4200")
public class TransactionController {

    private final TransactionServicePort transactionServicePort;

    public TransactionController(TransactionServicePort transactionServicePort) {
        this.transactionServicePort = transactionServicePort;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody CreateTransactionRequest request) {
        Transaction domainModel = toDomain(request);
        Transaction createdTransaction = transactionServicePort.createTransaction(domainModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(createdTransaction));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(@PathVariable Long accountId) {
        List<TransactionResponse> transactions = transactionServicePort.getTransactionsByAccountId(accountId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}/statement")
    public ResponseEntity<List<TransactionResponse>> getAccountStatement(@PathVariable Long accountId) {
        List<TransactionResponse> statement = transactionServicePort.getAccountStatement(accountId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(statement);
    }

    // Mappers internos HTTP Request/Response <-> Domain
    private Transaction toDomain(CreateTransactionRequest req) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(req.getTransactionType());
        transaction.setAmount(req.getAmount());
        transaction.setDescription(req.getDescription());
        transaction.setSourceAccountId(req.getSourceAccountId());
        transaction.setTargetAccountId(req.getTargetAccountId());
        return transaction;
    }

    private TransactionResponse toResponse(Transaction domain) {
        return new TransactionResponse(
                domain.getId(),
                domain.getTransactionType(),
                domain.getAmount(),
                domain.getDescription(),
                domain.getSourceAccountId(),
                domain.getTargetAccountId(),
                domain.getCreatedAt()
        );
    }
}
