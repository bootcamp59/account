package com.bootcamp.account.domain.dto;

import com.bootcamp.account.domain.enums.TransactionType;
import com.bootcamp.account.domain.model.Account;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

    private String id;
    private Account origen;
    private Account destino;

    private TransactionType type;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be positive")
    private Double amount;

    private String description;
    private LocalDateTime transactionDate;

    private Double transactionCommission;
}
