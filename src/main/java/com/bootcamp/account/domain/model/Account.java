package com.bootcamp.account.domain.model;

import com.bootcamp.account.domain.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    private String id;
    private String productoId;
    private String document;
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

    //solo para enviar a transacion
    private String cardNumber;

    public void deposit(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Deposit must be positive");
        }
        this.saldo = this.saldo + amount;
    }

    public void withdraw(double amount) {
        if (amount < 0){
            throw new IllegalArgumentException("Retiro must be positive");
        }
        if(amount > this.saldo){
            throw new RuntimeException("Saldo insuficiente para retirar");
        }
        this.saldo = this.saldo - amount;
    }

    public void transfer(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Deposit must be positive");
        }
        this.saldo = this.saldo + amount;
    }

}
