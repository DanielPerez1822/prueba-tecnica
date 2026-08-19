package com.trinity.prueba.infraestructure.adapter.out.persistence.adapter;

import com.trinity.prueba.domain.model.Client;
import com.trinity.prueba.domain.port.out.ClientRepositoryPort;
import com.trinity.prueba.infraestructure.adapter.out.persistence.entity.ClientEntity;
import com.trinity.prueba.infraestructure.adapter.out.persistence.mapper.ClientPersistenceMapper;
import com.trinity.prueba.infraestructure.adapter.out.persistence.repository.JpaClientRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ClientPersistenceAdapter implements ClientRepositoryPort {

    private final JpaClientRepository jpaClientRepository;
    private final ClientPersistenceMapper mapper;

    public ClientPersistenceAdapter(JpaClientRepository jpaClientRepository, ClientPersistenceMapper mapper) {
        this.jpaClientRepository = jpaClientRepository;
        this.mapper = mapper;
    }

    @Override
    public Client save(Client client) {
        ClientEntity entity = mapper.toEntity(client);
        ClientEntity savedEntity = jpaClientRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Client> findById(Long id) {
        return jpaClientRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Client> findAll() {
        return jpaClientRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaClientRepository.deleteById(id);
    }

    @Override
    public boolean existsByIdentificationNumber(Long identificationNumber) {
        return jpaClientRepository.existsByIdentificationNumber(identificationNumber);
    }
}
