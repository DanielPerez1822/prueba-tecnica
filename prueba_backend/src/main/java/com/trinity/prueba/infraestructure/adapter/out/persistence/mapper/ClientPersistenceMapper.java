package com.trinity.prueba.infraestructure.adapter.out.persistence.mapper;

import com.trinity.prueba.domain.model.Client;
import com.trinity.prueba.infraestructure.adapter.out.persistence.entity.ClientEntity;
import org.springframework.stereotype.Component;

@Component
public class ClientPersistenceMapper {

    public ClientEntity toEntity(Client domain) {
        if (domain == null) {
            return null;
        }
        return new ClientEntity(
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

    public Client toDomain(ClientEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Client(
                entity.getId(),
                entity.getIdentificationType(),
                entity.getIdentificationNumber(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getBirthDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
