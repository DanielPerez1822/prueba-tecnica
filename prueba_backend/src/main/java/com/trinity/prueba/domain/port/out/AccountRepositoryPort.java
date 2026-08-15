package com.trinity.prueba.domain.port.out;

public interface AccountRepositoryPort {

    boolean existsByClientId(Long clientId);
}
