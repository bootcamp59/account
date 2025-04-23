package com.bootcamp.account.infrastructure.adapter.out.client;

import com.bootcamp.account.aplication.port.out.TransactionServiceClientPort;
import com.bootcamp.account.domain.dto.TransactionDto;
import com.bootcamp.account.infrastructure.config.AccoountProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceAdapter implements TransactionServiceClientPort {

    private final WebClient.Builder webClientBuilder;
    private final AccoountProperties properties;

    @Override
    public Mono<Object> saveMovements(TransactionDto transactionDto) {
        var url = properties.getMsTransactionApi();

        return webClientBuilder.build()
                .post()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto)
                .retrieve()
                .bodyToMono(Object.class)
                .onErrorMap( e -> new RuntimeException("error al enviar la transaccion"))
                .doOnError(o -> log.error("Error al enviar la transaccion"));
    }
}
