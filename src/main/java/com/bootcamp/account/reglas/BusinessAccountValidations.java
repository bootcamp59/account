package com.bootcamp.account.reglas;

import com.bootcamp.account.enums.AccountType;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class BusinessAccountValidations {
    public static AccountValidation noSavingsAccounts() {
        return (account, customer, repo) ->
                account.getType() == AccountType.AHORRO
                        ? Mono.error(new RuntimeException("Empresas no pueden tener cuentas de ahorro"))
                        : Mono.empty();
    }

    public static AccountValidation noFixedTermAccounts() {
        return (account, customer, repo) ->
                account.getType() == AccountType.PLAZO_FIJO
                        ? Mono.error(new RuntimeException("Empresas no pueden tener plazos fijos"))
                        : Mono.empty();
    }

    public static AccountValidation atLeastOneHolder() {
        return (account, customer, repo) ->  Optional.ofNullable(account.getTitulares())
            .filter(titulares -> !titulares.isEmpty())
            .map(titulares -> Mono.<Void>empty())
            .orElseGet(() -> Mono.error(new RuntimeException("Se requiere al menos 1 titular")));
    }

}
