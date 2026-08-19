package com.trinity.prueba.domain.port.in;

import com.trinity.prueba.domain.model.Transaction;

import java.util.List;

public interface TransactionServicePort {

    /**
     * Crea y ejecuta una transacción financiera (DEPOSIT, WITHDRAWAL, TRANSFER).
     */
    Transaction createTransaction(Transaction transaction);

    /**
     * Obtiene todas las transacciones vinculadas a una cuenta.
     */
    List<Transaction> getTransactionsByAccountId(Long accountId);

    /**
     * Obtiene el estado de cuenta (historial de transacciones de la cuenta).
     */
    List<Transaction> getAccountStatement(Long accountId);
}
