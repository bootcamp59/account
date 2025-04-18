package com.bootcamp.account.infrastructure.adapter.out.persistence.mapper;

import com.bootcamp.account.domain.model.Account;
import com.bootcamp.account.infrastructure.adapter.out.persistence.entity.AccountEntity;


public class AccountEntityMapper {

    public static Account toModel(AccountEntity entity){
        return Account.builder()
                .id(entity.getId())
                .productoId(entity.getProductoId())
                .document(entity.getDni())
                .type(entity.getType())
                .comisionMantenimiento(entity.getComisionMantenimiento())
                .limiteMovimientosMensual(entity.getLimiteMovimientosMensual())
                .diaMovimientoPermitido(entity.getDiaMovimientoPermitido())
                .fechaApertura(entity.getFechaApertura())
                .banco(entity.getBanco())
                .titulares(entity.getTitulares())
                .firmantes(entity.getFirmantes())
                .saldo(entity.getSaldo())
                .montoNimimoPromedioMensual(entity.getMontoNimimoPromedioMensual())
                .transaccionesSinComision(entity.getTransaccionesSinComision())
                .comisionPorTransaccionExcedente(entity.getComisionPorTransaccionExcedente())
                .build();
    }

    public static AccountEntity toEntity(Account model){
        return AccountEntity.builder()
                .id(model.getId())
                .productoId(model.getProductoId())
                .dni(model.getDocument())
                .type(model.getType())
                .comisionMantenimiento(model.getComisionMantenimiento())
                .limiteMovimientosMensual(model.getLimiteMovimientosMensual())
                .diaMovimientoPermitido(model.getDiaMovimientoPermitido())
                .fechaApertura(model.getFechaApertura())
                .banco(model.getBanco())
                .titulares(model.getTitulares())
                .firmantes(model.getFirmantes())
                .saldo(model.getSaldo())
                .montoNimimoPromedioMensual(model.getMontoNimimoPromedioMensual())
                .transaccionesSinComision(model.getTransaccionesSinComision())
                .comisionPorTransaccionExcedente(model.getComisionPorTransaccionExcedente())
                .build();
    }
}
