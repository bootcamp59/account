package com.bootcamp.account.aplication.reglas;

import com.bootcamp.account.infrastructure.adapter.out.client.CreditServiceAdapter;
import com.bootcamp.account.domain.enums.AccountType;
import com.bootcamp.account.domain.enums.PerfilType;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
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

    public static AccountValidation requiredCreditCard(CreditServiceAdapter creditServiceAdapter) {
        return (account, customer, repo) -> {
            if(account.getType() == AccountType.CUENTA_CORRIENTE){
                if(customer.getPerfil() == PerfilType.PYME){
                    return creditServiceAdapter.findByCustomerDocNumber(customer.getDocNumber())
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
            if(account.getType() == AccountType.CUENTA_CORRIENTE && customer.getPerfil() == PerfilType.NORMAL && (account.getComisionMantenimiento() == 0)){
                return Mono.error(new RuntimeException("Cuenta corriente debe tener una comisión por mantenimiento"));
            } else {
                return Mono.empty();
            }
        };
    }

    public static AccountValidation withoutMaintenanceFee() {
        return (account, customer, repo) -> {
            if(account.getType() == AccountType.CUENTA_CORRIENTE && customer.getPerfil() == PerfilType.PYME && account.getComisionMantenimiento() > 0){
                return Mono.error(new RuntimeException("Cuenta corriente empresarial PYME no debe tener comision de mantenimiento"));
            } else {
                return Mono.empty();
            }
        };
    }

    //tercera entrega
    public static AccountValidation OverdueDebt(CreditServiceAdapter creditServiceAdapter){
        return (account, customer, repo) -> {
            return creditServiceAdapter.getOverDueDebit(customer.getDocNumber())
                    .hasElements()
                    .flatMap(hastDebt -> {
                        if (hastDebt) {
                            return Mono.error(new RuntimeException("No puede adquitir un producto por que tiene una deuda vencida"));
                        } else {
                            return Mono.empty();
                        }
                    });

        };
    }

}
