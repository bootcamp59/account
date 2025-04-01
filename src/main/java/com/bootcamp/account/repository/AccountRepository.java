package com.bootcamp.account.repository;

import com.bootcamp.account.model.entity.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {
    Flux<Account> findByCustomerId(String customerId);
    Flux<Account> findByCustomerIdAndType(String customerId, Account.AccountType type);
    Mono<Account> findByAccountNumber(String accountNumber);
    Mono<Boolean> existsByAccountNumber(String accountNumber);

}
