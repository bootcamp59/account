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
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService implements AccountUseCase {

    private final AccountRepositoryPort port;
    private final WebClient.Builder webClientBuilder;
    private final CreditoClient creditoClient;

    @Override
    public Mono<Account> create(Account model) {
        validaciones(model);
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
        return port.findByDni(dni);
    }

    @Override
    public Mono<DepositDto> deposit(DepositDto dto) {
        return port.findByProductoId(dto.getDestinyProductId())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Account not found for productId: " + dto.getDestinyProductId())))
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
        return port.findByProductoId(dto.getDestinyProductId())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Account not found for productId: " + dto.getDestinyProductId())))
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

    @Override
    public Mono<DepositDto> transfer(DepositDto dto) {

        var origenMono = port.findByProductoId(dto.getOriginProductId());
        var destinoMono = port.findByProductoId(dto.getDestinyProductId());

        return Mono.zip(origenMono, destinoMono)
            .flatMap( tuple -> {
                Account origen = tuple.getT1();
                Account destino = tuple.getT2();

                log.info("Iniciando transferencia de {} desde producto {} hacia producto {}",
                        dto.getAmount(), dto.getOriginProductId(), dto.getDestinyProductId());

                if(!origen.getDocument().equals(destino.getDocument())){
                    if(dto.getTipo().equals("transfer") && !origen.getBanco().equals(destino.getBanco())){
                        String error = String.format("Transferencia rechazada: solo se permiten transferencias a terceros dentro del mismo banco.");
                        log.warn(error);
                        return Mono.error(new IllegalStateException(error));
                    }
                }

                if(origen.getSaldo() < dto.getAmount()){
                    String error = String.format("Saldo insuficiente en el producto origen [%s]", dto.getOriginProductId());
                    log.warn(error);
                    return Mono.error(new IllegalStateException(error));
                }

                origen.withdraw(dto.getAmount());
                destino.transfer(dto.getAmount());

                return Mono.when(
                    port.update(origen)
                        .doOnSuccess(o -> log.info("Producto origen [{}] actualizado correctamente.", origen.getProductoId()))
                        .doOnError(e -> log.error("Error al actualizar producto origen [{}]: {}", origen.getProductoId(), e.getMessage())),
                    port.update(destino)
                        .doOnSuccess(d -> log.info("Producto destino [{}] actualizado correctamente.", destino.getProductoId()))
                        .doOnError(e -> log.error("Error al actualizar producto destino [{}]: {}", destino.getProductoId(), e.getMessage()))
                ).thenReturn(dto)
                .flatMap(result -> {
                    var movementDto = buildTransactionRequestTransfer(dto, origen, destino, TransactionType.TRANSFER);
                    return saveMovements(movementDto);
                });
            }).thenReturn(dto);
    }

    @Override
    public Flux<Account> findByProductoIdIn(List<String> productoIds) {
        return port.findByProductoIdIn(productoIds);
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
                .productoId(dto.getOriginProductId())
                .build())
            .destino(Account.builder()
                .productoId(dto.getDestinyProductId())
                .document(account.getDocument())
                .transaccionesSinComision(account.getTransaccionesSinComision())
                .banco(account.getBanco())
                .type(account.getType())
                .build())
            .transactionCommission(account.getComisionPorTransaccionExcedente())
            .build();
    }

    private TransactionDto buildTransactionRequestTransfer(DepositDto dto,Account origen, Account destino, TransactionType transactionType){
        return TransactionDto.builder()
                .amount(dto.getAmount())
                .type(transactionType)
                .description(dto.getDescription())
                .origen(Account.builder()
                        .document(origen.getDocument())
                        .cardNumber(dto.getCardNumber())
                        .productoId(origen.getProductoId())
                        .banco(origen.getBanco())
                        .type(origen.getType())
                        .build())
                .destino(Account.builder()
                        .productoId(dto.getDestinyProductId())
                        .document(destino.getDocument())
                        .cardNumber(destino.getCardNumber())
                        .transaccionesSinComision(destino.getTransaccionesSinComision())
                        .banco(destino.getBanco())
                        .type(destino.getType())
                        .build())
                .transactionCommission(destino.getComisionPorTransaccionExcedente())
                .build();
    }

    private void validaciones(Account model){
        if(model.getTransaccionesSinComision() == 0){
            throw new RuntimeException("Debe registrar cantidad mayor a 0 en  transacciones sin comision");
        }
        if(model.getComisionPorTransaccionExcedente() == 0){
            throw new RuntimeException("Debe registrar el valor de la comision por sobrepasar el limite de transacciones");
        }
    }
}
