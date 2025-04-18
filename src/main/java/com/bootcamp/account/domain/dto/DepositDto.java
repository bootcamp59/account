package com.bootcamp.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DepositDto {
    private double amount;
    private String productId;
    private String document;
    private String description;
}
