package com.trinity.prueba.infraestructure.adapter.in.rest;

import com.trinity.prueba.application.dto.request.CreateClientRequest;
import com.trinity.prueba.application.dto.request.UpdateClientRequest;
import com.trinity.prueba.application.dto.response.ClientResponse;
import com.trinity.prueba.domain.model.Client;
import com.trinity.prueba.domain.port.in.ClientServicePort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientServicePort clientServicePort;

    public ClientController(ClientServicePort clientServicePort) {
        this.clientServicePort = clientServicePort;
    }

    @PostMapping
    public ResponseEntity<ClientResponse> createClient(@RequestBody CreateClientRequest request) {
        Client domainModel = toDomain(request);
        Client createdClient = clientServicePort.createClient(domainModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(createdClient));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> updateClient(@PathVariable Long id, @RequestBody UpdateClientRequest request) {
        Client domainModel = toDomain(request);
        Client updatedClient = clientServicePort.updateClient(id, domainModel);
        return ResponseEntity.ok(toResponse(updatedClient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientServicePort.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Long id) {
        return clientServicePort.getClientById(id)
                .map(client -> ResponseEntity.ok(toResponse(client)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        List<ClientResponse> clients = clientServicePort.getAllClients().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(clients);
    }

    // Mappers internos HTTP Request/Response <-> Domain
    private Client toDomain(CreateClientRequest req) {
        Client client = new Client();
        client.setIdentificationType(req.getIdentificationType());
        client.setIdentificationNumber(req.getIdentificationNumber());
        client.setFirstName(req.getFirstName());
        client.setLastName(req.getLastName());
        client.setEmail(req.getEmail());
        client.setBirthDate(req.getBirthDate());
        return client;
    }

    private Client toDomain(UpdateClientRequest req) {
        Client client = new Client();
        client.setIdentificationType(req.getIdentificationType());
        client.setIdentificationNumber(req.getIdentificationNumber());
        client.setFirstName(req.getFirstName());
        client.setLastName(req.getLastName());
        client.setEmail(req.getEmail());
        client.setBirthDate(req.getBirthDate());
        return client;
    }

    private ClientResponse toResponse(Client domain) {
        return new ClientResponse(
                domain.getId(),
                domain.getIdentificationType(),
                domain.getIdentificationNumber(),
                domain.getFirstName(),
                domain.getLastName(),
                domain.getEmail(),
                domain.getBirthDate(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
