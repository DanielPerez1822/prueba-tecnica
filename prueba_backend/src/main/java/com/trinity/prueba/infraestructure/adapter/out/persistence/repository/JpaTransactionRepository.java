package com.trinity.prueba.infraestructure.adapter.out.persistence.repository;

import com.trinity.prueba.infraestructure.adapter.out.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaTransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findBySourceAccountId(Long sourceAccountId);

    List<TransactionEntity> findByTargetAccountId(Long targetAccountId);

    @Query("SELECT t FROM TransactionEntity t WHERE t.sourceAccountId = :accountId OR t.targetAccountId = :accountId ORDER BY t.createdAt DESC")
    List<TransactionEntity> findByAccountId(@Param("accountId") Long accountId);
}
