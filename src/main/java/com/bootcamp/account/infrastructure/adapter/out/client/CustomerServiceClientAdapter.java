package com.bootcamp.account.infrastructure.adapter.out.client;

import com.bootcamp.account.aplication.port.out.CustomerServiceClientPort;
import com.bootcamp.account.domain.dto.CustomerDto;
import com.bootcamp.account.domain.model.Account;
import com.bootcamp.account.infrastructure.config.AccoountProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomerServiceClientAdapter implements CustomerServiceClientPort {

    private final WebClient.Builder webClientBuilder;
    private final AccoountProperties properties;

    @Override
    public Mono<CustomerDto> fetchCustomerData(Account account) {
        var url = properties.getMsCustomerApi() + "/" + account.getDocument();

        return webClientBuilder.build()
            .get()
            .uri(url)
            .retrieve()
            .bodyToMono(CustomerDto.class)
            .map(customer -> customer);
    }
}
