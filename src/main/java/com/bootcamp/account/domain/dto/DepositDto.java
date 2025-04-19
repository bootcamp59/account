package com.bootcamp.account.domain.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DepositDto {
    private double amount;
    private String originProductId;
    private String destinyProductId;
    private String document;
    private String description;
}
