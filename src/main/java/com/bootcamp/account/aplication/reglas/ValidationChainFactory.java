package com.bootcamp.account.aplication.reglas;

import com.bootcamp.account.infrastructure.adapter.out.client.CreditServiceAdapter;
import com.bootcamp.account.domain.enums.CustomerType;

import java.util.List;

public class ValidationChainFactory {
    public static AccountValidationChain forCustomerType(CustomerType type, CreditServiceAdapter creditServiceAdapter) {
        return new AccountValidationChain(
            type == CustomerType.PERSONAL
                ? personalValidations(creditServiceAdapter)
                : businessValidations(creditServiceAdapter)
        );
    }

    private static List<AccountValidation> personalValidations(CreditServiceAdapter creditServiceAdapter) {
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
            PersonalAccountValidations.requiredPromedioMinimoDiarioMensual(creditServiceAdapter),
            PersonalAccountValidations.requiredCreditCard(creditServiceAdapter),
            PersonalAccountValidations.OverdueDebt(creditServiceAdapter)

        );
    }

    private static List<AccountValidation> businessValidations(CreditServiceAdapter creditServiceAdapter) {
        return List.of(
            BusinessAccountValidations.noSavingsAccounts(),
            BusinessAccountValidations.noFixedTermAccounts(),
            BusinessAccountValidations.withMaintenanceFee(),
            BusinessAccountValidations.atLeastOneHolder(),
            BusinessAccountValidations.withoutMaintenanceFee(),
            BusinessAccountValidations.requiredCreditCard(creditServiceAdapter),
            BusinessAccountValidations.OverdueDebt(creditServiceAdapter)
        );
    }


}
