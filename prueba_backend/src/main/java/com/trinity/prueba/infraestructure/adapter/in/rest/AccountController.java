package com.trinity.prueba.infraestructure.adapter.in.rest;

import com.trinity.prueba.application.dto.request.CreateAccountRequest;
import com.trinity.prueba.application.dto.request.UpdateAccountStatusRequest;
import com.trinity.prueba.application.dto.response.AccountResponse;
import com.trinity.prueba.domain.model.Account;
import com.trinity.prueba.domain.port.in.AccountServicePort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountServicePort accountServicePort;

    public AccountController(AccountServicePort accountServicePort) {
        this.accountServicePort = accountServicePort;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        Account domainModel = toDomain(request);
        Account createdAccount = accountServicePort.createAccount(domainModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(createdAccount));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountServicePort.getAllAccounts().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {
        return accountServicePort.getAccountById(id)
                .map(account -> ResponseEntity.ok(toResponse(account)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByClientId(@PathVariable Long clientId) {
        List<AccountResponse> accounts = accountServicePort.getAccountsByClientId(clientId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AccountResponse> updateAccountStatus(@PathVariable Long id,
                                                               @RequestBody UpdateAccountStatusRequest request) {
        Account updatedAccount = accountServicePort.updateAccountStatus(id, request.getStatus());
        return ResponseEntity.ok(toResponse(updatedAccount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAccount(@PathVariable Long id) {
        accountServicePort.cancelAccount(id);
        return ResponseEntity.ok().build();
    }

    // Mappers internos HTTP Request/Response <-> Domain
    private Account toDomain(CreateAccountRequest req) {
        Account account = new Account();
        account.setAccountType(req.getAccountType());
        account.setClientId(req.getClientId());
        account.setGmfExempt(req.isGmfExempt());
        return account;
    }

    private AccountResponse toResponse(Account domain) {
        return new AccountResponse(
                domain.getId(),
                domain.getAccountType(),
                domain.getAccountNumber(),
                domain.getStatus(),
                domain.getBalance(),
                domain.isGmfExempt(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getClientId()
        );
    }
}
