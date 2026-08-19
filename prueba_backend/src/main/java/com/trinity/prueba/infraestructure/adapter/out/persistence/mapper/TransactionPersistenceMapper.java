package com.trinity.prueba.infraestructure.adapter.out.persistence.mapper;

import com.trinity.prueba.domain.model.Transaction;
import com.trinity.prueba.infraestructure.adapter.out.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionPersistenceMapper {

    public TransactionEntity toEntity(Transaction domain) {
        if (domain == null) {
            return null;
        }
        return new TransactionEntity(
                domain.getId(),
                domain.getTransactionType(),
                domain.getAmount(),
                domain.getDescription(),
                domain.getSourceAccountId(),
                domain.getTargetAccountId(),
                domain.getCreatedAt()
        );
    }

    public Transaction toDomain(TransactionEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Transaction(
                entity.getId(),
                entity.getTransactionType(),
                entity.getAmount(),
                entity.getDescription(),
                entity.getSourceAccountId(),
                entity.getTargetAccountId(),
                entity.getCreatedAt()
        );
    }
}
