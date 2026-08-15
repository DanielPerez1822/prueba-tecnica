package com.trinity.prueba.infraestructure.adapter.out.persistence.repository;

import com.trinity.prueba.infraestructure.adapter.out.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaAccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    List<AccountEntity> findByClientId(Long clientId);

    boolean existsByClientId(Long clientId);

    boolean existsByAccountNumber(String accountNumber);
}
