package com.trinity.prueba.infraestructure.config;

import com.trinity.prueba.application.service.AccountService;
import com.trinity.prueba.application.service.ClientService;
import com.trinity.prueba.application.service.TransactionService;
import com.trinity.prueba.domain.port.in.AccountServicePort;
import com.trinity.prueba.domain.port.in.ClientServicePort;
import com.trinity.prueba.domain.port.in.TransactionServicePort;
import com.trinity.prueba.domain.port.out.AccountRepositoryPort;
import com.trinity.prueba.domain.port.out.ClientRepositoryPort;
import com.trinity.prueba.domain.port.out.TransactionRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ClientServicePort clientServicePort(ClientRepositoryPort clientRepositoryPort,
                                               AccountRepositoryPort accountRepositoryPort) {
        return new ClientService(clientRepositoryPort, accountRepositoryPort);
    }

    @Bean
    public AccountServicePort accountServicePort(AccountRepositoryPort accountRepositoryPort,
                                                 ClientRepositoryPort clientRepositoryPort) {
        return new AccountService(accountRepositoryPort, clientRepositoryPort);
    }

    @Bean
    public TransactionServicePort transactionServicePort(TransactionRepositoryPort transactionRepositoryPort,
                                                         AccountRepositoryPort accountRepositoryPort) {
        return new TransactionService(transactionRepositoryPort, accountRepositoryPort);
    }
}
