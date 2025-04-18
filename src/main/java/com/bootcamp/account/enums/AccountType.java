package com.bootcamp.account.enums;

import com.bootcamp.account.model.dto.CustomerDto;
import com.bootcamp.account.repository.AccountRepositoryDepre;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

public enum AccountType {
    AHORRO(buildAhorroValidator()),

    CUENTA_CORRIENTE(buildCorrienteValidator()),

    PLAZO_FIJO(buildplazoFijoValidator());

    private final BiFunction<CustomerDto, AccountRepositoryDepre, Mono<Void>> validator;

    AccountType(BiFunction<CustomerDto, AccountRepositoryDepre, Mono<Void>> validator) {
        this.validator = validator;
    }

    public Mono<Void> validate(CustomerDto customer, AccountRepositoryDepre repo) {
        return validator.apply(customer, repo);
    }

    private static BiFunction<CustomerDto, AccountRepositoryDepre, Mono<Void>> buildAhorroValidator() {
        return (customer, repo) -> {
            return customer.getType() != CustomerType.PERSONAL
                ? Mono.error(new RuntimeException("Solo clientes personales"))
                : repo.findByCustomerIdAndType(customer.getId(), AHORRO)
                .count()
                    .flatMap(count -> {
                        if (count > 0) {
                            return Mono.error(new IllegalArgumentException(
                                    "Maximo una cuenta de ahorro"));
                        }
                        return Mono.empty();
                    });
        };
    }

    private static BiFunction<CustomerDto, AccountRepositoryDepre, Mono<Void>> buildCorrienteValidator() {
        return (customer, repo) -> customer.getType() == CustomerType.PERSONAL
                ? repo.findByCustomerIdAndType(customer.getId(), CUENTA_CORRIENTE)
                .count()
                .flatMap(count -> {
                    if (count > 0) {
                        return Mono.error(new IllegalArgumentException("Maximo una cuenta CORRIENTE"));
                    }
                    return Mono.empty();
                })
                : Mono.empty();
    }

    private static BiFunction<CustomerDto, AccountRepositoryDepre, Mono<Void>> buildplazoFijoValidator() {
        return (customer, repo) -> {
            return customer.getType() != CustomerType.PERSONAL
                ? Mono.error(new RuntimeException("Solo clientes personales"))
                : Mono.empty();
        };
    }


}
