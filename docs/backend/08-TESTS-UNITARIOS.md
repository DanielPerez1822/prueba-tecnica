# 🧪 Tests Unitarios — Backend

## 1. Estrategia de Testing

Los tests unitarios cubren las capas **Service** y **Controller** usando **JUnit 5** y **Mockito**, garantizando que la lógica de negocio y los endpoints funcionen correctamente de forma aislada.

---

## 2. Stack de Testing

| Herramienta | Propósito |
|-------------|-----------|
| **JUnit 5** | Framework de testing principal |
| **Mockito** | Mocking de dependencias |
| **MockMvc** | Testing de controladores REST |
| **AssertJ** | Aserciones fluidas y legibles |
| **Spring Boot Test** | Integración con el contexto de Spring |

### Dependencias Maven

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 3. Estructura de Tests

```
src/test/java/com/trinity/prueba/
├── application/
│   └── service/
│       ├── ClientServiceTest.java          # Tests del servicio de clientes
│       ├── AccountServiceTest.java         # Tests del servicio de cuentas
│       └── TransactionServiceTest.java     # Tests del servicio de transacciones
├── infraestructure/
│   └── adapter/
│       └── in/
│           └── rest/
│               ├── ClientControllerTest.java       # Tests del controller de clientes
│               ├── AccountControllerTest.java       # Tests del controller de cuentas
│               └── TransactionControllerTest.java   # Tests del controller de transacciones
└── domain/
    └── model/
        ├── ClientTest.java                 # Tests de la entidad de dominio
        ├── AccountTest.java                # Tests de la entidad de dominio
        └── TransactionTest.java            # Tests de la entidad de dominio
```

---

## 4. Tests de la Capa Service

### `ClientServiceTest.java`

```java
package com.trinity.prueba.application.service;

import com.trinity.prueba.domain.model.Client;
import com.trinity.prueba.domain.model.exception.ClientHasProductsException;
import com.trinity.prueba.domain.model.exception.ClientNotFoundException;
import com.trinity.prueba.domain.model.exception.ClientUnderageException;
import com.trinity.prueba.domain.port.out.AccountRepositoryPort;
import com.trinity.prueba.domain.port.out.ClientRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientService Tests")
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
        validClient.setFirstName("Juan");
        validClient.setLastName("Pérez");
        validClient.setEmail("juan@email.com");
        validClient.setBirthDate(LocalDate.of(1990, 5, 15));
        validClient.setIdentificationType("CC");
        validClient.setIdentificationNumber("1234567890");
    }

    // =============================================
    // CREAR CLIENTE
    // =============================================
    @Nested
    @DisplayName("Crear Cliente")
    class CreateClientTests {

        @Test
        @DisplayName("Debe crear un cliente válido exitosamente")
        void shouldCreateValidClient() {
            // Arrange
            when(clientRepository.save(any(Client.class))).thenReturn(validClient);

            // Act
            Client result = clientService.createClient(validClient);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getFirstName()).isEqualTo("Juan");
            verify(clientRepository, times(1)).save(any(Client.class));
        }

        @Test
        @DisplayName("Debe asignar fecha de creación automáticamente")
        void shouldSetCreatedAtAutomatically() {
            // Arrange
            when(clientRepository.save(any(Client.class))).thenReturn(validClient);

            // Act
            clientService.createClient(validClient);

            // Assert
            assertThat(validClient.getCreatedAt()).isNotNull();
            assertThat(validClient.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Debe lanzar excepción si el cliente es menor de edad")
        void shouldThrowExceptionWhenClientIsUnderage() {
            // Arrange
            validClient.setBirthDate(LocalDate.now().minusYears(15));

            // Act & Assert
            assertThatThrownBy(() -> clientService.createClient(validClient))
                .isInstanceOf(ClientUnderageException.class)
                .hasMessageContaining("mayor de edad");

            verify(clientRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el email es inválido")
        void shouldThrowExceptionWhenEmailIsInvalid() {
            // Arrange
            validClient.setEmail("email-invalido");

            // Act & Assert
            assertThatThrownBy(() -> clientService.createClient(validClient))
                .isInstanceOf(InvalidClientDataException.class)
                .hasMessageContaining("correo electrónico");
        }

        @Test
        @DisplayName("Debe lanzar excepción si el nombre tiene menos de 2 caracteres")
        void shouldThrowExceptionWhenNameIsTooShort() {
            // Arrange
            validClient.setFirstName("J");

            // Act & Assert
            assertThatThrownBy(() -> clientService.createClient(validClient))
                .isInstanceOf(InvalidClientDataException.class)
                .hasMessageContaining("2 caracteres");
        }
    }

    // =============================================
    // ACTUALIZAR CLIENTE
    // =============================================
    @Nested
    @DisplayName("Actualizar Cliente")
    class UpdateClientTests {

        @Test
        @DisplayName("Debe actualizar un cliente existente")
        void shouldUpdateExistingClient() {
            // Arrange
            validClient.setId(1L);
            when(clientRepository.findById(1L)).thenReturn(Optional.of(validClient));
            when(clientRepository.save(any(Client.class))).thenReturn(validClient);

            Client updatedData = new Client();
            updatedData.setFirstName("Carlos");
            updatedData.setLastName("García");
            updatedData.setEmail("carlos@email.com");
            updatedData.setBirthDate(LocalDate.of(1985, 3, 20));
            updatedData.setIdentificationType("CC");
            updatedData.setIdentificationNumber("9876543210");

            // Act
            Client result = clientService.updateClient(1L, updatedData);

            // Assert
            assertThat(result.getUpdatedAt()).isNotNull();
            verify(clientRepository).save(any(Client.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción si el cliente no existe")
        void shouldThrowExceptionWhenClientNotFound() {
            // Arrange
            when(clientRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> clientService.updateClient(99L, validClient))
                .isInstanceOf(ClientNotFoundException.class);
        }
    }

    // =============================================
    // ELIMINAR CLIENTE
    // =============================================
    @Nested
    @DisplayName("Eliminar Cliente")
    class DeleteClientTests {

        @Test
        @DisplayName("Debe eliminar un cliente sin productos")
        void shouldDeleteClientWithoutProducts() {
            // Arrange
            when(clientRepository.findById(1L)).thenReturn(Optional.of(validClient));
            when(accountRepository.existsByClientId(1L)).thenReturn(false);

            // Act
            clientService.deleteClient(1L);

            // Assert
            verify(clientRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Debe lanzar excepción si el cliente tiene productos vinculados")
        void shouldThrowExceptionWhenClientHasProducts() {
            // Arrange
            when(clientRepository.findById(1L)).thenReturn(Optional.of(validClient));
            when(accountRepository.existsByClientId(1L)).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> clientService.deleteClient(1L))
                .isInstanceOf(ClientHasProductsException.class)
                .hasMessageContaining("productos financieros vinculados");

            verify(clientRepository, never()).deleteById(any());
        }
    }

    // =============================================
    // CONSULTAR CLIENTES
    // =============================================
    @Nested
    @DisplayName("Consultar Clientes")
    class GetClientTests {

        @Test
        @DisplayName("Debe obtener un cliente por ID")
        void shouldGetClientById() {
            // Arrange
            when(clientRepository.findById(1L)).thenReturn(Optional.of(validClient));

            // Act
            Optional<Client> result = clientService.getClientById(1L);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getFirstName()).isEqualTo("Juan");
        }

        @Test
        @DisplayName("Debe retornar vacío si el cliente no existe")
        void shouldReturnEmptyWhenClientNotFound() {
            // Arrange
            when(clientRepository.findById(99L)).thenReturn(Optional.empty());

            // Act
            Optional<Client> result = clientService.getClientById(99L);

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Debe obtener todos los clientes")
        void shouldGetAllClients() {
            // Arrange
            Client client2 = new Client();
            client2.setFirstName("María");
            when(clientRepository.findAll()).thenReturn(Arrays.asList(validClient, client2));

            // Act
            List<Client> result = clientService.getAllClients();

            // Assert
            assertThat(result).hasSize(2);
        }
    }
}
```

### `TransactionServiceTest.java` (Resumen)

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @Mock
    private AccountRepositoryPort accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Nested
    @DisplayName("Consignación (Depósito)")
    class DepositTests {

        @Test
        @DisplayName("Debe realizar una consignación exitosa")
        void shouldExecuteDepositSuccessfully() {
            // Arrange
            Account account = createActiveAccount(1L, "5312345678", BigDecimal.valueOf(1000));
            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

            Transaction deposit = new Transaction();
            deposit.setTransactionType(TransactionType.DEPOSIT);
            deposit.setAmount(BigDecimal.valueOf(500));
            deposit.setTargetAccountId(1L);

            when(transactionRepository.save(any())).thenReturn(deposit);
            when(accountRepository.save(any())).thenReturn(account);

            // Act
            Transaction result = transactionService.createTransaction(deposit);

            // Assert
            assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1500));
            verify(accountRepository).save(account);
        }
    }

    @Nested
    @DisplayName("Transferencia")
    class TransferTests {

        @Test
        @DisplayName("Debe realizar una transferencia exitosa entre cuentas")
        void shouldExecuteTransferSuccessfully() {
            // Arrange
            Account source = createActiveAccount(1L, "5312345678", BigDecimal.valueOf(1000));
            Account target = createActiveAccount(2L, "3387654321", BigDecimal.valueOf(500));

            when(accountRepository.findById(1L)).thenReturn(Optional.of(source));
            when(accountRepository.findById(2L)).thenReturn(Optional.of(target));

            Transaction transfer = new Transaction();
            transfer.setTransactionType(TransactionType.TRANSFER);
            transfer.setAmount(BigDecimal.valueOf(300));
            transfer.setSourceAccountId(1L);
            transfer.setTargetAccountId(2L);

            when(transactionRepository.save(any())).thenReturn(transfer);
            when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // Act
            transactionService.createTransaction(transfer);

            // Assert
            assertThat(source.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(700));
            assertThat(target.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(800));
            verify(accountRepository, times(2)).save(any(Account.class));
        }

        @Test
        @DisplayName("Debe fallar si el saldo es insuficiente en cuenta de ahorros")
        void shouldFailWhenInsufficientBalance() {
            // Arrange
            Account source = createActiveAccount(1L, "5312345678", BigDecimal.valueOf(100));
            source.setAccountType(AccountType.SAVINGS);

            when(accountRepository.findById(1L)).thenReturn(Optional.of(source));
            when(accountRepository.findById(2L)).thenReturn(
                Optional.of(createActiveAccount(2L, "3387654321", BigDecimal.valueOf(500))));

            Transaction transfer = new Transaction();
            transfer.setTransactionType(TransactionType.TRANSFER);
            transfer.setAmount(BigDecimal.valueOf(500));
            transfer.setSourceAccountId(1L);
            transfer.setTargetAccountId(2L);

            // Act & Assert
            assertThatThrownBy(() -> transactionService.createTransaction(transfer))
                .isInstanceOf(InsufficientBalanceException.class);
        }
    }
}
```

---

## 5. Tests de la Capa Controller

### `ClientControllerTest.java`

```java
package com.trinity.prueba.infraestructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trinity.prueba.domain.model.Client;
import com.trinity.prueba.domain.model.exception.ClientUnderageException;
import com.trinity.prueba.domain.port.in.ClientServicePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
@DisplayName("ClientController Tests")
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientServicePort clientService;

    @Test
    @DisplayName("POST /api/v1/clients - Debe crear un cliente y retornar 201")
    void shouldCreateClientAndReturn201() throws Exception {
        // Arrange
        Client client = createValidClient();
        client.setId(1L);
        client.setCreatedAt(LocalDateTime.now());
        when(clientService.createClient(any(Client.class))).thenReturn(client);

        // Act & Assert
        mockMvc.perform(post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(client)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("Juan"))
            .andExpect(jsonPath("$.lastName").value("Pérez"))
            .andExpect(jsonPath("$.email").value("juan@email.com"));
    }

    @Test
    @DisplayName("POST /api/v1/clients - Debe retornar 400 si es menor de edad")
    void shouldReturn400WhenUnderage() throws Exception {
        // Arrange
        Client client = createValidClient();
        client.setBirthDate(LocalDate.now().minusYears(15));
        when(clientService.createClient(any(Client.class)))
            .thenThrow(new ClientUnderageException("El cliente debe ser mayor de edad"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(client)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/clients - Debe listar todos los clientes")
    void shouldGetAllClients() throws Exception {
        // Arrange
        when(clientService.getAllClients())
            .thenReturn(Arrays.asList(createValidClient(), createValidClient()));

        // Act & Assert
        mockMvc.perform(get("/api/v1/clients"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/v1/clients/{id} - Debe obtener un cliente por ID")
    void shouldGetClientById() throws Exception {
        // Arrange
        Client client = createValidClient();
        client.setId(1L);
        when(clientService.getClientById(1L)).thenReturn(Optional.of(client));

        // Act & Assert
        mockMvc.perform(get("/api/v1/clients/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("Juan"));
    }

    @Test
    @DisplayName("GET /api/v1/clients/{id} - Debe retornar 404 si no existe")
    void shouldReturn404WhenClientNotFound() throws Exception {
        // Arrange
        when(clientService.getClientById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/clients/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/clients/{id} - Debe eliminar y retornar 204")
    void shouldDeleteClientAndReturn204() throws Exception {
        // Arrange
        doNothing().when(clientService).deleteClient(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/clients/1"))
            .andExpect(status().isNoContent());

        verify(clientService).deleteClient(1L);
    }

    private Client createValidClient() {
        Client client = new Client();
        client.setFirstName("Juan");
        client.setLastName("Pérez");
        client.setEmail("juan@email.com");
        client.setBirthDate(LocalDate.of(1990, 5, 15));
        client.setIdentificationType("CC");
        client.setIdentificationNumber("1234567890");
        return client;
    }
}
```

---

## 6. Cobertura de Tests

### Matriz de Cobertura

| Capa | Clase | Tests | Cobertura Objetivo |
|------|-------|-------|--------------------|
| **Service** | `ClientService` | 10+ | ≥ 80% |
| **Service** | `AccountService` | 10+ | ≥ 80% |
| **Service** | `TransactionService` | 12+ | ≥ 80% |
| **Controller** | `ClientController` | 6+ | ≥ 80% |
| **Controller** | `AccountController` | 6+ | ≥ 80% |
| **Controller** | `TransactionController` | 5+ | ≥ 80% |
| **Domain** | `Client` | 5+ | ≥ 90% |
| **Domain** | `Account` | 8+ | ≥ 90% |

### Ejecutar Tests

```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar tests con reporte de cobertura (JaCoCo)
./mvnw test jacoco:report

# Ejecutar tests de una clase específica
./mvnw test -Dtest=ClientServiceTest

# Ejecutar tests con verbose output
./mvnw test -Dsurefire.useFile=false
```

---

## 7. Convenciones de Testing

| Convención | Ejemplo |
|------------|---------|
| Nomenclatura de método | `shouldCreateValidClient()`, `shouldThrowExceptionWhenUnderage()` |
| Patrón AAA | **Arrange** → **Act** → **Assert** |
| `@DisplayName` | Siempre incluir descripción legible en español |
| `@Nested` | Agrupar tests por funcionalidad |
| `@BeforeEach` | Preparar datos comunes |
| Mock de dependencias | Usar `@Mock` + `@InjectMocks` |
| Sin acceso a BD | Tests unitarios NO acceden a la base de datos |
