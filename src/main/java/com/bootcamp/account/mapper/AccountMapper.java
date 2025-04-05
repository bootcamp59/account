package com.bootcamp.account.mapper;

import com.bootcamp.account.model.dto.AccountDto;
import com.bootcamp.account.model.entity.Account;

public class AccountMapper {

    public static Account dtoToEntity(AccountDto dto){
        return Account.builder()
                .customerId(dto.getCustomerId())
                .accountNumber(dto.getAccountNumber())
                .type(dto.getType())
                .titulares(dto.getTitulares())
                .authorizedSigners(dto.getAuthorizedSigners())
                .allowedDayOfMonth(dto.getAllowedDayOfMonth())
                .maintenanceFee(dto.getMaintenanceFee())
                .monthlyTransactionLimit(dto.getMonthlyTransactionLimit())
                .build();
    }

}
