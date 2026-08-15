package com.trinity.prueba.infraestructure.config;

import com.trinity.prueba.application.service.ClientService;
import com.trinity.prueba.domain.port.in.ClientServicePort;
import com.trinity.prueba.domain.port.out.AccountRepositoryPort;
import com.trinity.prueba.domain.port.out.ClientRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ClientServicePort clientServicePort(ClientRepositoryPort clientRepositoryPort,
                                               AccountRepositoryPort accountRepositoryPort) {
        return new ClientService(clientRepositoryPort, accountRepositoryPort);
    }
}
