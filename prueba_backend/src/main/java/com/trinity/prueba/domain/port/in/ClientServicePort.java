package com.trinity.prueba.domain.port.in;

import com.trinity.prueba.domain.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientServicePort {

    Client createClient(Client client);

    Client updateClient(Long id, Client client);

    void deleteClient(Long id);

    Optional<Client> getClientById(Long id);

    List<Client> getAllClients();
}
