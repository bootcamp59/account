package com.bootcamp.account.reglas;

import com.bootcamp.account.client.CreditoClient;
import com.bootcamp.account.enums.AccountType;
import com.bootcamp.account.enums.CustomerType;
import com.bootcamp.account.model.entity.Account;
import reactor.core.publisher.Mono;

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
          if(account.getAuthorizedSigners() != null && !account.getAuthorizedSigners().isEmpty()){
              return Mono.error(new RuntimeException("Solo cuentas empresariales pueden tener firmantes"));
          } else {
              return Mono.<Void>empty();
          }
        };

    }

    public static AccountValidation requiredCreditCard(CreditoClient creditoClient) {
        return (account, customer, repo) -> {
            var url = "http://localhost:8087/api/v1/credit/customer/" + customer.getId();
            if(account.getType() == AccountType.AHORRO){
                if(customer.getPerfil().equalsIgnoreCase("VIP")){
                    return creditoClient.get(url)
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




}
