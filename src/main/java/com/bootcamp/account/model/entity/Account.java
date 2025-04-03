package com.bootcamp.account.model.entity;

import com.bootcamp.account.enums.AccountType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "account")
public class Account {
    @Id
    private String id;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotNull(message = "Account type is required")
    private AccountType type;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @Min(value = 0, message = "Balance cannot be negative")
    private double balance;

    private LocalDateTime openingDate;
    private LocalDateTime lastTransactionDate;

    // Specific fields for each account type
    private Double maintenanceFee; // Only for checking accounts libre mantenimiento
    private Integer monthlyTransactionLimit; // limite de movimientos mensuales
    private Integer allowedDayOfMonth; //dia del mes para su deposito o retiro

    private List<String> holders; // For business accounts (multiple holders) titulares
    private List<String> authorizedSigners; // For business accounts firmantes


}
