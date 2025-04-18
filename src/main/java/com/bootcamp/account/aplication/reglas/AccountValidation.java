package com.bootcamp.account.aplication.reglas;

import com.bootcamp.account.aplication.port.out.AccountRepositoryPort;
import com.bootcamp.account.domain.model.Account;
import com.bootcamp.account.domain.dto.CustomerDto;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface AccountValidation {
    Mono<Void> validate(Account account, CustomerDto customer, AccountRepositoryPort repo);
}
