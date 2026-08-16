package com.trinity.prueba.application.service;

import com.trinity.prueba.domain.model.Client;
import com.trinity.prueba.domain.model.enums.IdentificationType;
import com.trinity.prueba.domain.model.exception.ClientHasProductsException;
import com.trinity.prueba.domain.model.exception.ClientNotFoundException;
import com.trinity.prueba.domain.model.exception.ClientUnderageException;
import com.trinity.prueba.domain.model.exception.InvalidClientDataException;
import com.trinity.prueba.domain.port.out.AccountRepositoryPort;
import com.trinity.prueba.domain.port.out.ClientRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepositoryPort clientRepository;

    @Mock
    private AccountRepositoryPort accountRepository;

    @InjectMocks
    private ClientService clientService;

    private Client validClient;

    @BeforeEach
    void setUp() {
        validClient = new Client();
        validClient.setId(1L);
        validClient.setIdentificationType(IdentificationType.CC);
        validClient.setIdentificationNumber(1001234567L);
        validClient.setFirstName("Juan");
        validClient.setLastName("Perez");
        validClient.setEmail("juan.perez@email.com");
        validClient.setBirthDate(LocalDate.now().minusYears(25));
    }

    @Test
    @DisplayName("Crear cliente exitosamente asignando fechas automáticamente")
    void createClient_success() {
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Client created = clientService.createClient(validClient);

        assertNotNull(created);
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
        verify(clientRepository, times(1)).save(validClient);
    }

    @Test
    @DisplayName("Lanzar excepción al intentar crear cliente menor de edad")
    void createClient_underage_throwsClientUnderageException() {
        validClient.setBirthDate(LocalDate.now().minusYears(17));

        assertThrows(ClientUnderageException.class, () -> clientService.createClient(validClient));
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lanzar excepción al intentar crear cliente con email inválido")
    void createClient_invalidEmail_throwsInvalidClientDataException() {
        validClient.setEmail("email-invalido");

        assertThrows(InvalidClientDataException.class, () -> clientService.createClient(validClient));
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lanzar excepción al intentar crear cliente con nombre corto (< 2 caracteres)")
    void createClient_invalidName_throwsInvalidClientDataException() {
        validClient.setFirstName("A");

        assertThrows(InvalidClientDataException.class, () -> clientService.createClient(validClient));
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar cliente exitosamente recalculando updatedAt")
    void updateClient_success() {
        Client existing = new Client();
        existing.setId(1L);
        existing.setBirthDate(LocalDate.now().minusYears(30));

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Client updated = clientService.updateClient(1L, validClient);

        assertNotNull(updated);
        assertEquals("Juan", updated.getFirstName());
        assertNotNull(updated.getUpdatedAt());
        verify(clientRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Lanzar excepción al actualizar cliente no existente")
    void updateClient_notFound_throwsClientNotFoundException() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.updateClient(99L, validClient));
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar cliente sin productos vinculados exitosamente")
    void deleteClient_success() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(validClient));
        when(accountRepository.existsByClientId(1L)).thenReturn(false);

        clientService.deleteClient(1L);

        verify(clientRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Lanzar excepción al eliminar cliente que tiene productos vinculados")
    void deleteClient_hasProducts_throwsClientHasProductsException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(validClient));
        when(accountRepository.existsByClientId(1L)).thenReturn(true);

        assertThrows(ClientHasProductsException.class, () -> clientService.deleteClient(1L));
        verify(clientRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Obtener cliente por ID")
    void getClientById_success() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(validClient));

        Optional<Client> result = clientService.getClientById(1L);

        assertTrue(result.isPresent());
        assertEquals("Juan", result.get().getFirstName());
    }

    @Test
    @DisplayName("Obtener todos los clientes")
    void getAllClients_success() {
        when(clientRepository.findAll()).thenReturn(List.of(validClient));

        List<Client> result = clientService.getAllClients();

        assertEquals(1, result.size());
    }
}
