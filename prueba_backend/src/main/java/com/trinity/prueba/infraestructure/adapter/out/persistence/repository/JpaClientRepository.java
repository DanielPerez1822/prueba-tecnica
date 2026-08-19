package com.trinity.prueba.infraestructure.adapter.out.persistence.repository;

import com.trinity.prueba.infraestructure.adapter.out.persistence.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaClientRepository extends JpaRepository<ClientEntity, Long> {

    boolean existsByIdentificationNumber(Long identificationNumber);
}
