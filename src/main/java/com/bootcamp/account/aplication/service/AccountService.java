package com.bootcamp.account.aplication.service;

import com.bootcamp.account.aplication.port.in.AccountUseCase;
import com.bootcamp.account.aplication.port.out.AccountRepositoryPort;
import com.bootcamp.account.aplication.reglas.AccountValidationChain;
import com.bootcamp.account.aplication.reglas.ValidationChainFactory;
import com.bootcamp.account.client.CreditoClient;
import com.bootcamp.account.domain.dto.DepositDto;
import com.bootcamp.account.domain.dto.TransactionDto;
import com.bootcamp.account.domain.enums.TransactionType;
import com.bootcamp.account.domain.model.Account;
import com.bootcamp.account.domain.dto.CustomerDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService implements AccountUseCase {

    private final AccountRepositoryPort port;
    private final WebClient.Builder webClientBuilder;
    private final CreditoClient creditoClient;

    @Override
    public Mono<Account> create(Account model) {
        return Mono.just(model)
            .flatMap(this::fetchCustomerData)
            .flatMap(customer -> {
                AccountValidationChain chain = ValidationChainFactory.forCustomerType(customer.getType(), creditoClient);
                return chain.execute(model, customer, port)
                        .then(saveNewAccount(model));
            });
    }

    @Override
    public Flux<Account> findAll() {
        return port.findAll();
    }

    @Override
    public Flux<Account> findByDni(String dni) {
        return findByDni(dni);
    }

    @Override
    public Mono<DepositDto> deposit(DepositDto dto) {
        return port.findByProductoId(dto.getProductId())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Account not found for productId: " + dto.getProductId())))
            .flatMap( account -> {
                account.deposit(dto.getAmount());
                return port.update(account);
            })
            .flatMap(account -> {
                var movementDto = buildTransactionRequest(dto, account, TransactionType.DEPOSIT);
                return saveMovements(movementDto);
            })
            .thenReturn(dto)
            .onErrorResume(error -> {
                System.out.println("Error during deposit: " + error.getMessage());
                return Mono.error(new RuntimeException("Error while processing deposit: " + error.getMessage()));
            });
    }

    @Override
    public Mono<DepositDto> withdraw(DepositDto dto) {
        return port.findByProductoId(dto.getProductId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Account not found for productId: " + dto.getProductId())))
                .flatMap( account -> {
                    account.withdraw(dto.getAmount());
                    return port.update(account);
                })
                .flatMap(account -> {
                    var movementDto = buildTransactionRequest(dto, account, TransactionType.RETIRO);
                    return saveMovements(movementDto);
                })
                .thenReturn(dto)
                .onErrorResume(error -> {
                    System.out.println("Error during deposit: " + error.getMessage());
                    return Mono.error(new RuntimeException("Error while processing deposit: " + error.getMessage()));
                });
    }

    private Mono<CustomerDto> fetchCustomerData(Account account) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8085/api/v1/customer/customers/{docNumber}", account.getDocument())
                .retrieve()
                .bodyToMono(CustomerDto.class)
                .map(customer -> customer);
    }

    private Mono<Account> saveNewAccount(Account account) {
        account.setFechaApertura(LocalDateTime.now());
        account.setFechaUltimaTransacion(LocalDateTime.now());
        return port.create(account);
    }

    private Mono<Object> saveMovements(TransactionDto transactionDto){
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8084/api/v1/transaction")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto)
                .retrieve()
                .bodyToMono(Object.class)
                .onErrorMap( e -> new RuntimeException("error al enviar la transaccion"))
                .doOnError(o -> log.error("Error al enviar la transaccion"));
    }

    private TransactionDto buildTransactionRequest(DepositDto dto, Account account, TransactionType transactionType){
        return TransactionDto.builder()
                .amount(dto.getAmount())
                .type(transactionType)
                .description(dto.getDescription())
                .origen(Account.builder()
                        .document(dto.getDocument())
                        .build())
                .destino(Account.builder()
                        .productoId(dto.getProductId())
                        .document(account.getDocument())
                        .banco(account.getBanco())
                        .type(account.getType())
                        .build())
                .build();
    }
}
