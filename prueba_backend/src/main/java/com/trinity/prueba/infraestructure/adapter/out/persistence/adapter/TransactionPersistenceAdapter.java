package com.trinity.prueba.infraestructure.adapter.out.persistence.adapter;

import com.trinity.prueba.domain.model.Transaction;
import com.trinity.prueba.domain.port.out.TransactionRepositoryPort;
import com.trinity.prueba.infraestructure.adapter.out.persistence.entity.TransactionEntity;
import com.trinity.prueba.infraestructure.adapter.out.persistence.mapper.TransactionPersistenceMapper;
import com.trinity.prueba.infraestructure.adapter.out.persistence.repository.JpaTransactionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TransactionPersistenceAdapter implements TransactionRepositoryPort {

    private final JpaTransactionRepository jpaTransactionRepository;
    private final TransactionPersistenceMapper mapper;

    public TransactionPersistenceAdapter(JpaTransactionRepository jpaTransactionRepository,
                                         TransactionPersistenceMapper mapper) {
        this.jpaTransactionRepository = jpaTransactionRepository;
        this.mapper = mapper;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = mapper.toEntity(transaction);
        TransactionEntity savedEntity = jpaTransactionRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return jpaTransactionRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Transaction> findBySourceAccountId(Long accountId) {
        return jpaTransactionRepository.findBySourceAccountId(accountId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findByTargetAccountId(Long accountId) {
        return jpaTransactionRepository.findByTargetAccountId(accountId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findByAccountId(Long accountId) {
        return jpaTransactionRepository.findByAccountId(accountId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
