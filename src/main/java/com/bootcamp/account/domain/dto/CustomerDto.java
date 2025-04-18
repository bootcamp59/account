package com.bootcamp.account.domain.dto;

import com.bootcamp.account.domain.enums.CustomerType;
import com.bootcamp.account.domain.enums.PerfilType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
    private PerfilType perfil;
}
