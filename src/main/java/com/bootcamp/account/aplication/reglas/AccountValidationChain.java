package com.bootcamp.account.aplication.reglas;

import com.bootcamp.account.aplication.port.out.AccountRepositoryPort;
import com.bootcamp.account.domain.model.Account;
import com.bootcamp.account.domain.dto.CustomerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class AccountValidationChain {
    private final List<AccountValidation> validations;

    public AccountValidationChain(List<AccountValidation> validations) {
        this.validations = validations;
    }

    public Mono<Void> execute(Account account, CustomerDto customer, AccountRepositoryPort repo) {
        return Flux.fromIterable(validations)
                .concatMap(validation -> validation.validate(account, customer, repo))
                .then();
    }
}
