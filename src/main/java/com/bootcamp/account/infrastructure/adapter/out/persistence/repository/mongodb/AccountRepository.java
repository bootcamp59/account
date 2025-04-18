package com.bootcamp.account.infrastructure.adapter.out.persistence.repository.mongodb;


import com.bootcamp.account.domain.enums.AccountType;
import com.bootcamp.account.infrastructure.adapter.out.persistence.entity.AccountEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveMongoRepository<AccountEntity, String> {
    Flux<AccountEntity> findByDni(String dni);
    Mono<AccountEntity> findByProductoId(String id);
    Flux<AccountEntity> findByDniAndType(String document, AccountType type);
}
