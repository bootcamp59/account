package com.bootcamp.account.aplication.port.out;

import com.bootcamp.account.domain.dto.TransactionDto;
import reactor.core.publisher.Mono;

public interface TransactionServiceClientPort {

    Mono<Object> saveMovements(TransactionDto transactionDto);
}
