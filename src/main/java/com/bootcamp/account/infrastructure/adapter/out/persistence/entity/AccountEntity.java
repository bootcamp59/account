package com.bootcamp.account.infrastructure.adapter.out.persistence.entity;

import com.bootcamp.account.domain.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "account")
public class AccountEntity {

    @Id
    private String id;
    private String productoId;
    private String dni;
    private AccountType type;
    private double comisionMantenimiento;
    private double limiteMovimientosMensual;
    private double diaMovimientoPermitido;
    private LocalDateTime fechaApertura;
    private String banco;
    private Set<String> titulares;
    private Set<String> firmantes;
    private double saldo;
    private double montoNimimoPromedioMensual;
    private double transaccionesSinComision;
    private double comisionPorTransaccionExcedente;
    private LocalDateTime fechaUltimaTransacion;

}
