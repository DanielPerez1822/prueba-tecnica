package com.trinity.prueba.infraestructure.adapter.out.persistence.adapter;

import com.trinity.prueba.domain.port.out.AccountRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class AccountPersistenceAdapterStub implements AccountRepositoryPort {

    @Override
    public boolean existsByClientId(Long clientId) {
        // En esta primera iteración no existen productos/cuentas creados en BD aún.
        // Retorna false para permitir la eliminación de clientes durante las pruebas de este módulo.
        return false;
    }
}
