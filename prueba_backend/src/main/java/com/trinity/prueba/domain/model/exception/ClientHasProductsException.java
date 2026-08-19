package com.trinity.prueba.domain.model.exception;

public class ClientHasProductsException extends RuntimeException {
    public ClientHasProductsException(String message) {
        super(message);
    }
}
