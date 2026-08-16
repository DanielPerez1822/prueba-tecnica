package com.trinity.prueba.infraestructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.trinity.prueba.application.dto.request.CreateClientRequest;
import com.trinity.prueba.application.dto.request.UpdateClientRequest;
import com.trinity.prueba.domain.model.Client;
import com.trinity.prueba.domain.model.enums.IdentificationType;
import com.trinity.prueba.domain.port.in.ClientServicePort;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClientServicePort clientServicePort;

    @InjectMocks
    private ClientController clientController;

    private ObjectMapper objectMapper;
    private Client sampleClient;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleClient = new Client();
        sampleClient.setId(1L);
        sampleClient.setIdentificationType(IdentificationType.CC);
        sampleClient.setIdentificationNumber(1001234567L);
        sampleClient.setFirstName("Juan");
        sampleClient.setLastName("Perez");
        sampleClient.setEmail("juan.perez@email.com");
        sampleClient.setBirthDate(LocalDate.of(1990, 5, 15));
    }

    @Test
    @DisplayName("POST /api/v1/clients debe retornar status 201 Created")
    void createClient_returnsCreated() throws Exception {
        CreateClientRequest request = new CreateClientRequest(
                IdentificationType.CC, 1001234567L, "Juan", "Perez",
                "juan.perez@email.com", LocalDate.of(1990, 5, 15)
        );

        when(clientServicePort.createClient(any(Client.class))).thenReturn(sampleClient);

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan.perez@email.com"));
    }

    @Test
    @DisplayName("PUT /api/v1/clients/{id} debe retornar status 200 OK")
    void updateClient_returnsOk() throws Exception {
        UpdateClientRequest request = new UpdateClientRequest(
                IdentificationType.CC, 1001234567L, "Juan Carlos", "Perez",
                "juan.perez@email.com", LocalDate.of(1990, 5, 15)
        );

        sampleClient.setFirstName("Juan Carlos");
        when(clientServicePort.updateClient(eq(1L), any(Client.class))).thenReturn(sampleClient);

        mockMvc.perform(put("/api/v1/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Juan Carlos"));
    }

    @Test
    @DisplayName("GET /api/v1/clients/{id} debe retornar status 200 OK cuando existe")
    void getClientById_exists_returnsOk() throws Exception {
        when(clientServicePort.getClientById(1L)).thenReturn(Optional.of(sampleClient));

        mockMvc.perform(get("/api/v1/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Juan"));
    }

    @Test
    @DisplayName("GET /api/v1/clients/{id} debe retornar status 404 Not Found cuando no existe")
    void getClientById_notFound_returns404() throws Exception {
        when(clientServicePort.getClientById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/clients/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/clients debe retornar status 200 OK con la lista de clientes")
    void getAllClients_returnsOk() throws Exception {
        when(clientServicePort.getAllClients()).thenReturn(List.of(sampleClient));

        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Juan"));
    }

    @Test
    @DisplayName("DELETE /api/v1/clients/{id} debe retornar status 204 No Content")
    void deleteClient_returnsNoContent() throws Exception {
        doNothing().when(clientServicePort).deleteClient(1L);

        mockMvc.perform(delete("/api/v1/clients/1"))
                .andExpect(status().isNoContent());
    }
}
