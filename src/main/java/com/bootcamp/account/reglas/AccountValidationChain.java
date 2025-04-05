package com.bootcamp.account.reglas;

import com.bootcamp.account.model.dto.CustomerDto;
import com.bootcamp.account.model.entity.Account;
import com.bootcamp.account.repository.AccountRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class AccountValidationChain {
    private final List<AccountValidation> validations;

    public AccountValidationChain(List<AccountValidation> validations) {
        this.validations = validations;
    }

    public Mono<Void> execute(Account account, CustomerDto customer, AccountRepository repo) {
        return Flux.fromIterable(validations)
                .concatMap(validation -> validation.validate(account, customer, repo))
                .then();
    }
}
