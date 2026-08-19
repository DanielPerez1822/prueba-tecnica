package com.trinity.prueba.domain.port.out;

import com.trinity.prueba.domain.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryPort {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(Long id);

    List<Transaction> findBySourceAccountId(Long accountId);

    List<Transaction> findByTargetAccountId(Long accountId);

    List<Transaction> findByAccountId(Long accountId);
}
