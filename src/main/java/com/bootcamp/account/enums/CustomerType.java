package com.bootcamp.account.enums;

import java.util.Map;
import java.util.Set;

public enum CustomerType {

    PERSONAL(Map.of(
            AccountType.AHORRO, 1,
            AccountType.CUENTA_CORRIENTE, 1,
            AccountType.PLAZO_FIJO, Integer.MAX_VALUE  // Ilimitadas
    )),

    BUSINESS(Map.of(
            AccountType.AHORRO, 0,   // No puede tener
            AccountType.CUENTA_CORRIENTE, Integer.MAX_VALUE,  // Ilimitadas
            AccountType.PLAZO_FIJO, 0   // No puede tener
    ));

    private final Map<AccountType, Integer> accountLimits;

    CustomerType(Map<AccountType, Integer> accountLimits) {
        this.accountLimits = accountLimits;
    }

    public boolean canOpenAccount(Set<AccountType> existingAccounts, AccountType newAccount) {
        int currentCount = (int) existingAccounts.stream().filter(a -> a == newAccount).count();
        return currentCount < accountLimits.getOrDefault(newAccount, 0);
    }

}
