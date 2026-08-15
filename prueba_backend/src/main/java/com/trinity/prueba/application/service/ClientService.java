package com.trinity.prueba.application.service;

import com.trinity.prueba.domain.model.Client;
import com.trinity.prueba.domain.model.exception.ClientHasProductsException;
import com.trinity.prueba.domain.model.exception.ClientNotFoundException;
import com.trinity.prueba.domain.model.exception.ClientUnderageException;
import com.trinity.prueba.domain.model.exception.InvalidClientDataException;
import com.trinity.prueba.domain.port.in.ClientServicePort;
import com.trinity.prueba.domain.port.out.AccountRepositoryPort;
import com.trinity.prueba.domain.port.out.ClientRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public class ClientService implements ClientServicePort {

    private final ClientRepositoryPort clientRepository;
    private final AccountRepositoryPort accountRepository;

    public ClientService(ClientRepositoryPort clientRepository,
                         AccountRepositoryPort accountRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public Client createClient(Client client) {
        // RN-01: No puede ser menor de edad
        if (client.isUnderage()) {
            throw new ClientUnderageException("El cliente debe ser mayor de edad (18 años o más).");
        }

        // RN-05: Validar formato de correo electrónico
        if (!client.hasValidEmail()) {
            throw new InvalidClientDataException("El correo electrónico debe tener un formato válido (xxxx@xxxxx.xxx).");
        }

        // RN-06: Longitud de nombre y apellido no menor a 2 caracteres
        if (!client.hasValidName()) {
            throw new InvalidClientDataException("El nombre y el apellido deben tener al menos 2 caracteres.");
        }

        // Validar que el número de identificación sea numérico positivo
        if (!client.hasValidIdentificationNumber()) {
            throw new InvalidClientDataException("El número de identificación debe ser un valor numérico válido mayor a cero.");
        }

        // RN-03: Asignar fecha de creación y modificación automática
        LocalDateTime now = LocalDateTime.now();
        client.setCreatedAt(now);
        client.setUpdatedAt(now);

        return clientRepository.save(client);
    }

    @Override
    @Transactional
    public Client updateClient(Long id, Client clientData) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado con ID: " + id));

        existingClient.setIdentificationType(clientData.getIdentificationType());
        existingClient.setIdentificationNumber(clientData.getIdentificationNumber());
        existingClient.setFirstName(clientData.getFirstName());
        existingClient.setLastName(clientData.getLastName());
        existingClient.setEmail(clientData.getEmail());
        existingClient.setBirthDate(clientData.getBirthDate());

        // RN-01: Validar mayoría de edad en caso de actualizar la fecha de nacimiento
        if (existingClient.isUnderage()) {
            throw new ClientUnderageException("El cliente debe ser mayor de edad (18 años o más).");
        }

        // RN-05: Validar email
        if (!existingClient.hasValidEmail()) {
            throw new InvalidClientDataException("El correo electrónico debe tener un formato válido (xxxx@xxxxx.xxx).");
        }

        // RN-06: Validar nombre y apellido
        if (!existingClient.hasValidName()) {
            throw new InvalidClientDataException("El nombre y el apellido deben tener al menos 2 caracteres.");
        }

        // Validar número de identificación
        if (!existingClient.hasValidIdentificationNumber()) {
            throw new InvalidClientDataException("El número de identificación debe ser un valor numérico válido mayor a cero.");
        }

        // RN-04: Fecha de modificación calculada automáticamente
        existingClient.setUpdatedAt(LocalDateTime.now());

        return clientRepository.save(existingClient);
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado con ID: " + id));

        // RN-02: Un cliente no podrá ser eliminado si tiene productos vinculados
        if (accountRepository.existsByClientId(id)) {
            throw new ClientHasProductsException("No se puede eliminar el cliente porque tiene productos financieros vinculados.");
        }

        clientRepository.deleteById(id);
    }

    @Override
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
}
