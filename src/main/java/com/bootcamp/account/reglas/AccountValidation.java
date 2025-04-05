package com.bootcamp.account.reglas;

import com.bootcamp.account.model.dto.CustomerDto;
import com.bootcamp.account.model.entity.Account;
import com.bootcamp.account.repository.AccountRepository;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface AccountValidation {
    Mono<Void> validate(Account account, CustomerDto customer, AccountRepository repo);
}
