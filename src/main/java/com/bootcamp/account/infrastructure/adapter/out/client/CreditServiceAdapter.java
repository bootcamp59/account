package com.bootcamp.account.infrastructure.adapter.out.client;

import com.bootcamp.account.infrastructure.config.AccoountProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@Component
public class CreditServiceAdapter {

    private final WebClient.Builder webClientBuilder;
    private final AccoountProperties properties;

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

    public Flux<Object> getOverDueDebit(String docNumber){
        var url = properties.getMsCustomerApi() + "/" + docNumber + "/debt";
        log.info("peticion a verificar deudas pendientes: "+ url);
        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Object.class)
                .doOnNext(f -> {
                    log.info("conexion exitosa al serivicio: {}", url + f);
                })
                .onErrorResume(e -> {
                    log.error("Error al conectar con el servicio de deudas: {}", e.getMessage());
                    return Flux.error(new RuntimeException("No se pudo conectar con: " + url));
                });
    }

    public Flux<Object> findByCustomerDocNumber(String docNumber){
        var url = properties.getMsCreditApi() + "/customer/" + docNumber;

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
