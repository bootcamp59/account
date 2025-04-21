package com.bootcamp.account.aplication.port.out;


import com.bootcamp.account.domain.enums.AccountType;
import com.bootcamp.account.domain.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountRepositoryPort {

    Mono<Account> create(Account model);
    Mono<Account> update(Account model);
    Flux<Account> findAll();
    Flux<Account> findByDni(String dni);
    Mono<Account> findByProductoId(String productoId);
    Flux<Account> findByDniAndType(String document, AccountType type);
    Flux<Account> findByProductoIdIn(List<String> productoIds);
}
