package com.bootcamp.account.aplication.reglas;

import com.bootcamp.account.client.CreditoClient;
import com.bootcamp.account.domain.enums.CustomerType;

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
            PersonalAccountValidations.noTieneTitulares(),
            PersonalAccountValidations.noTieneFirmantes(),
            PersonalAccountValidations.maintenanceFeeFreeCorriente(),
            PersonalAccountValidations.maintenanceFeeFreeAhorro(),
            PersonalAccountValidations.monthlyTransactionLimitAhorro(),
            PersonalAccountValidations.monthlyTransactionLimitCorriente(),
            PersonalAccountValidations.maintenanceFeeFreePlazoFijo(),
            PersonalAccountValidations.monthlyTransactionLimitPlazoFijo(),
            PersonalAccountValidations.freeMovementsTransactions(),
            PersonalAccountValidations.maxOneSavingsAccount(),
            PersonalAccountValidations.maxOneCheckingAccount(),
            PersonalAccountValidations.requiredPromedioMinimoDiarioMensual(creditoClient),
            PersonalAccountValidations.requiredCreditCard(creditoClient),
            PersonalAccountValidations.OverdueDebt(creditoClient)

        );
    }

    private static List<AccountValidation> businessValidations(CreditoClient creditoClient) {
        return List.of(
            BusinessAccountValidations.noSavingsAccounts(),
            BusinessAccountValidations.noFixedTermAccounts(),
            BusinessAccountValidations.withMaintenanceFee(),
            BusinessAccountValidations.atLeastOneHolder(),
            BusinessAccountValidations.withoutMaintenanceFee(),
            BusinessAccountValidations.requiredCreditCard(creditoClient),
            BusinessAccountValidations.OverdueDebt(creditoClient)
        );
    }


}
