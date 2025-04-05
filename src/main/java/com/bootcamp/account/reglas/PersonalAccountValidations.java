package com.bootcamp.account.reglas;

import com.bootcamp.account.enums.AccountType;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public class PersonalAccountValidations {

    public static AccountValidation maxOneSavingsAccount() {
        return (account, customer, repo) ->
            account.getType() == AccountType.AHORRO
                ? repo.findByCustomerIdAndType(customer.getId(), AccountType.AHORRO)
                .count()
                .flatMap(count -> {
                    if (count > 0) {
                        return Mono.error(new IllegalArgumentException("Solo 1 cuenta de ahorro permitida"));
                    }
                    return Mono.empty();
                })
                : Mono.empty();
    }

    public static AccountValidation maxOneCheckingAccount() {
        return (account, customer, repo) ->
            account.getType() == AccountType.CUENTA_CORRIENTE
                ? repo.findByCustomerIdAndType(customer.getId(), AccountType.CUENTA_CORRIENTE)
                .count()
                .flatMap(count -> {
                    if (count > 0) {
                        return Mono.error(new IllegalArgumentException("Solo 1 cuenta corriente permitida"));
                    }
                    return Mono.empty();
                })
                : Mono.empty();
    }

    public static AccountValidation noTieneTitulares() {
        return (account, customer, repo) ->
            Optional.ofNullable(account.getTitulares())
                .filter(List::isEmpty)
                .map(a -> Mono.<Void>empty())
                .orElseGet( () -> Mono.error(new RuntimeException("Solo cuentas empresariales pueden tener titulares")));
    }

    public static AccountValidation noTieneFirmantes() {
        return (account, customer, repo) ->
            Optional.ofNullable(account.getAuthorizedSigners())
                .filter(List::isEmpty)
                .map(a -> Mono.<Void>empty())
                .orElseGet( () -> Mono.error(new RuntimeException("Solo cuentas empresariales pueden tener firmantes")));
    }
}
