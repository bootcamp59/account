package com.bootcamp.account.reglas;

import com.bootcamp.account.client.CreditoClient;
import com.bootcamp.account.enums.CustomerType;

import java.util.List;

public class ValidationChainFactory {
    public static AccountValidationChain forCustomerType(CustomerType type, CreditoClient creditoClient) {
        return new AccountValidationChain(
            type == CustomerType.PERSONAL
                ? personalValidations(creditoClient)
                : businessValidations(creditoClient)
        );
    }

    private static List<AccountValidation> personalValidations(CreditoClient creditoClient) {
        return List.of(
            PersonalAccountValidations.maxOneSavingsAccount(),
            PersonalAccountValidations.maxOneCheckingAccount(),
            PersonalAccountValidations.noTieneTitulares(),
            PersonalAccountValidations.noTieneFirmantes(),
            PersonalAccountValidations.requiredPromedioMinimoDiarioMensual(creditoClient),
            PersonalAccountValidations.requiredCreditCard(creditoClient)

        );
    }

    private static List<AccountValidation> businessValidations(CreditoClient creditoClient) {
        return List.of(
            BusinessAccountValidations.noSavingsAccounts(),
            BusinessAccountValidations.noFixedTermAccounts(),
            BusinessAccountValidations.withMaintenanceFee(),
            BusinessAccountValidations.atLeastOneHolder(),
            BusinessAccountValidations.withoutMaintenanceFee(),
            BusinessAccountValidations.requiredCreditCard(creditoClient)
        );
    }


}
