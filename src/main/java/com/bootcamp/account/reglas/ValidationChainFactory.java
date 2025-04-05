package com.bootcamp.account.reglas;

import com.bootcamp.account.enums.CustomerType;

import java.util.List;

public class ValidationChainFactory {
    public static AccountValidationChain forCustomerType(CustomerType type) {
        return new AccountValidationChain(
            type == CustomerType.PERSONAL
                ? personalValidations()
                : businessValidations()
        );
    }

    private static List<AccountValidation> personalValidations() {
        return List.of(
            PersonalAccountValidations.maxOneSavingsAccount(),
            PersonalAccountValidations.maxOneCheckingAccount(),
            PersonalAccountValidations.noTieneTitulares(),
            PersonalAccountValidations.noTieneFirmantes()
        );
    }

    private static List<AccountValidation> businessValidations() {
        return List.of(
            BusinessAccountValidations.noSavingsAccounts(),
            BusinessAccountValidations.noFixedTermAccounts(),
            BusinessAccountValidations.atLeastOneHolder()
        );
    }
}
