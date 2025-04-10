package com.bootcamp.account.model.dto;

import com.bootcamp.account.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotNull(message = "Account type is required")
    private AccountType type;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    private double openingAmount;

    private double promedioDiarioMinimoMensual;

    private Integer maximoTransacionSinComision;
    private Double commissionRate;


    // Specific fields for each account type
    private Double maintenanceFee; // Only for checking accounts libre mantenimiento
    private Integer monthlyTransactionLimit; // limite de movimientos mensuales
    private Integer allowedDayOfMonth; //dia del mes para su deposito o retiro

    private List<String> titulares; // For business accounts (multiple holders) titulares
    private List<String> authorizedSigners; // For business accounts firmantes

}
