package com.trinity.prueba.domain.port.out;

import com.trinity.prueba.domain.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepositoryPort {

    Client save(Client client);

    Optional<Client> findById(Long id);

    List<Client> findAll();

    void deleteById(Long id);

    boolean existsByIdentificationNumber(Long identificationNumber);
}
