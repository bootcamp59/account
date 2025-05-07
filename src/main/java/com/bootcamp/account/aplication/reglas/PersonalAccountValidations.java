package com.bootcamp.account.aplication.reglas;

import com.bootcamp.account.infrastructure.adapter.out.client.CreditServiceAdapter;
import com.bootcamp.account.domain.enums.AccountType;
import com.bootcamp.account.domain.enums.PerfilType;
import reactor.core.publisher.Mono;

public class PersonalAccountValidations {

    public static AccountValidation noTieneTitulares() {
        return (account, customer, repo) -> {
            if(account.getTitulares() != null && !account.getTitulares().isEmpty()){
                return Mono.error(new RuntimeException("Solo cuentas empresariales pueden tener titulares"));
            } else {
                return Mono.<Void>empty();
            }
        };

    }

    public static AccountValidation noTieneFirmantes() {
        return (account, customer, repo) -> {
            if(account.getFirmantes() != null && !account.getFirmantes().isEmpty()){
                return Mono.error(new RuntimeException("Solo cuentas empresariales pueden tener firmantes"));
            } else {
                return Mono.<Void>empty();
            }
        };

    }

    public static AccountValidation maxOneSavingsAccount() {
        return (account, customer, repo) ->
            account.getType() == AccountType.AHORRO
                ? repo.findByDniAndType(customer.getDocNumber(), AccountType.AHORRO)
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
                ? repo.findByDniAndType(customer.getDocNumber(), AccountType.CUENTA_CORRIENTE)
                .count()
                .flatMap(count -> {
                    if (count > 0) {
                        return Mono.error(new IllegalArgumentException("Solo 1 cuenta corriente permitida"));
                    }
                    return Mono.empty();
                })
                : Mono.empty();
    }



    public static AccountValidation requiredCreditCard(CreditServiceAdapter creditServiceAdapter) {
        return (account, customer, repo) -> {
            if(account.getType() == AccountType.AHORRO){
                if(customer.getPerfil() == PerfilType.VIP){
                    return creditServiceAdapter.findByCustomerDocNumber(customer.getDocNumber())
                        .hasElements()
                        .flatMap(hasCredit -> {
                            if (hasCredit) {
                                return Mono.empty();
                            } else {
                                return Mono.error(new RuntimeException("Clientes VIP tienen que tener una tarjeta de credito previamente, para poder crear su cuenta de ahorro"));
                            }
                        });
                }

            }
            return Mono.empty();
        };
    }

    public static AccountValidation requiredPromedioMinimoDiarioMensual(CreditServiceAdapter creditServiceAdapter) {
        return (account, customer, repo) -> {
                if(account.getType() == AccountType.AHORRO && customer.getPerfil() == PerfilType.VIP && account.getMontoNimimoPromedioMensual() == 0){
                    return Mono.error(new RuntimeException("Cuenta de ahorro debe tener un monto minimo de promedio diario mensual"));
                } else {
                    return Mono.empty();
            }
        };
    }

    //primera entrega
    public static AccountValidation maintenanceFeeFreeAhorro() {
        return (account, customer, repo) -> {
            if(customer.getPerfil() == PerfilType.NORMAL && account.getType() == AccountType.AHORRO && account.getComisionMantenimiento() > 0){
                return Mono.error(new RuntimeException("Cuenta de ahorro debe estar libre de comisión por mantenimiento"));
            } else {
                return Mono.empty();
            }
        };
    }

    //primera entrega
    public static AccountValidation monthlyTransactionLimitAhorro() {
        return (account, customer, repo) -> {
            if(customer.getPerfil() == PerfilType.NORMAL && account.getType() == AccountType.AHORRO && account.getLimiteMovimientosMensual() == 0){
                return Mono.error(new RuntimeException("Cuenta de ahorro debe tener un limite maximo de movimientos mensual"));
            } else {
                return Mono.empty();
            }
        };
    }

    //primera entrega
    public static AccountValidation maintenanceFeeFreeCorriente() {
        return (account, customer, repo) -> {
            if(customer.getPerfil() == PerfilType.NORMAL && account.getType() == AccountType.CUENTA_CORRIENTE && account.getComisionMantenimiento() == 0){
                return Mono.error(new RuntimeException("Cuenta corriente debe tener comision de mantenimiento"));
            } else {
                return Mono.empty();
            }
        };
    }

    //primera entrega
    public static AccountValidation monthlyTransactionLimitCorriente() {
        return (account, customer, repo) -> {
            if(customer.getPerfil() == PerfilType.NORMAL && account.getType() == AccountType.CUENTA_CORRIENTE && account.getLimiteMovimientosMensual() > 0){
                return Mono.error(new RuntimeException("Cuenta corriente no debe tener limite de movimientos"));
            } else {
                return Mono.empty();
            }
        };
    }

    //primera entrega
    public static AccountValidation maintenanceFeeFreePlazoFijo() {
        return (account, customer, repo) -> {
            if(customer.getPerfil() == PerfilType.NORMAL && account.getType() == AccountType.PLAZO_FIJO && account.getComisionMantenimiento() > 0){
                return Mono.error(new RuntimeException("Cuenta Plazo fijo debe estar libre de comisión por mantenimient"));
            } else {
                return Mono.empty();
            }
        };
    }

    //primera entrega
    public static AccountValidation monthlyTransactionLimitPlazoFijo() {
        return (account, customer, repo) -> {
            if(customer.getPerfil() == PerfilType.NORMAL && account.getType() == AccountType.PLAZO_FIJO && account.getLimiteMovimientosMensual() != 1){
                return Mono.error(new RuntimeException("Cuenta Plazo fijo su limite de movimiento mensual debe ser 1"));
            } else {
                return Mono.empty();
            }
        };
    }

    //primera entrega
    public static AccountValidation freeMovementsTransactions() {
        return (account, customer, repo) -> {
            if(customer.getPerfil() == PerfilType.NORMAL && (account.getType() == AccountType.CUENTA_CORRIENTE || account.getType() == AccountType.AHORRO) && account.getDiaMovimientoPermitido() > 0){
                return Mono.error(new RuntimeException("Cuenta de ahorro y cuenta corriente no estan limitados a hacer un solo movimiento al mes"));
            } else {
                return Mono.empty();
            }
        };
    }

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
