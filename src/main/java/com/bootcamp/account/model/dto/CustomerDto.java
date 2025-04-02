package com.bootcamp.account.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {

    private String id;
    private String name;
    private CustomerType type;
    private String docType;
    private String docNumber;
    public enum CustomerType {
        PERSONAL, BUSINESS
    }
}
