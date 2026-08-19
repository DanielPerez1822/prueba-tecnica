package com.trinity.prueba.domain.model.exception;

public class ClientUnderageException extends RuntimeException {
    public ClientUnderageException(String message) {
        super(message);
    }
}
