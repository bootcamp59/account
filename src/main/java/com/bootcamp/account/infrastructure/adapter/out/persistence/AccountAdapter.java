package com.bootcamp.account.infrastructure.adapter.out.persistence;

import com.bootcamp.account.aplication.port.out.AccountRepositoryPort;
import com.bootcamp.account.domain.enums.AccountType;
import com.bootcamp.account.domain.model.Account;
import com.bootcamp.account.infrastructure.adapter.out.persistence.mapper.AccountEntityMapper;

import com.bootcamp.account.infrastructure.adapter.out.persistence.repository.mongodb.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AccountAdapter implements AccountRepositoryPort {

    private final AccountRepository repository;

    @Override
    public Mono<Account> create(Account model) {
        var entity = AccountEntityMapper.toEntity(model);
        return repository.save(entity)
            .map(AccountEntityMapper::toModel);
    }

    @Override
    public Mono<Account> update(Account model) {
        return repository.save(AccountEntityMapper.toEntity(model))
            .map(AccountEntityMapper::toModel);
    }

    @Override
    public Flux<Account> findAll() {
        return repository.findAll()
            .map(AccountEntityMapper::toModel);
    }

    @Override
    public Flux<Account> findByDni(String dni) {
        return repository.findByDni(dni)
            .map(AccountEntityMapper::toModel);
    }

    @Override
    public Mono<Account> findByProductoId(String productoId) {
        return repository.findByProductoId(productoId)
                .map(AccountEntityMapper::toModel);
    }

    @Override
    public Flux<Account> findByDniAndType(String document, AccountType type) {
        return repository.findByDniAndType(document, type)
            .map(AccountEntityMapper::toModel);
    }

    @Override
    public Flux<Account> findByProductoIdIn(List<String> productoIds) {
        return repository.findByProductoIdIn(productoIds)
            .map(AccountEntityMapper::toModel);
    }


}
