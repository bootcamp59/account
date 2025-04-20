package com.bootcamp.account.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class CreditoClient {

    private final WebClient.Builder webClientBuilder;

    public Flux<Object> get(String url){
        return webClientBuilder.build()
            .get()
            .uri(url)
            .retrieve()
            .bodyToFlux(Object.class)
            .doOnNext(f -> {
                log.info("conexion exitosa al serivicio: {}", url + f);
            })
            .onErrorReturn(new RuntimeException("no se pudo conectar con: " + url));
    }
}
