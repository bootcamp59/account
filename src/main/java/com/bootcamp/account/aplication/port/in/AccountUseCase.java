package com.bootcamp.account.aplication.port.in;


import com.bootcamp.account.domain.dto.DepositDto;
import com.bootcamp.account.domain.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountUseCase {

    Mono<Account> create(Account model);
    Flux<Account> findAll();
    Flux<Account> findByDni(String dni);
    Mono<DepositDto> deposit(DepositDto dto);
    Mono<DepositDto> withdraw(DepositDto dto);
}
