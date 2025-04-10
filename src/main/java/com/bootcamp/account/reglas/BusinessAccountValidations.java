package com.bootcamp.account.reglas;

import com.bootcamp.account.client.CreditoClient;
import com.bootcamp.account.enums.AccountType;
import com.bootcamp.account.enums.PerfilType;
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

    public static AccountValidation requiredCreditCard(CreditoClient creditoClient) {
        return (account, customer, repo) -> {
            var url = "http://localhost:8087/api/v1/credit/customer/" + customer.getId();
            if(account.getType() == AccountType.CUENTA_CORRIENTE){
                if(customer.getPerfil() == PerfilType.PYME){
                    return creditoClient.get(url)
                        .hasElements()
                        .flatMap(hasCredit -> {
                            if (hasCredit) {
                                return Mono.empty();
                            } else {
                                return Mono.error(new RuntimeException("Clientes PYME tienen que tener una tarjeta de credito previamente, para poder crear su cuenta corriente"));
                            }
                        });
                }

            }
            return Mono.empty();
        };
    }

    public static AccountValidation withMaintenanceFee() {
        return (account, customer, repo) -> {
            if(account.getType() == AccountType.CUENTA_CORRIENTE && customer.getPerfil() == PerfilType.NORMAL && ( account.getMaintenanceFee() == null || account.getMaintenanceFee() == 0)){
                return Mono.error(new RuntimeException("Cuenta corriente debe tener una comisión por mantenimiento"));
            } else {
                return Mono.empty();
            }
        };
    }

    public static AccountValidation withoutMaintenanceFee() {
        return (account, customer, repo) -> {
            if(account.getType() == AccountType.CUENTA_CORRIENTE && customer.getPerfil() == PerfilType.PYME && account.getMaintenanceFee() > 0){
                return Mono.error(new RuntimeException("Cuenta corriente empresarial PYME no debe tener comision de mantenimiento"));
            } else {
                return Mono.empty();
            }
        };
    }

}
