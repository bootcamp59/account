package com.bootcamp.account.aplication.port.out;

import com.bootcamp.account.domain.dto.CustomerDto;
import com.bootcamp.account.domain.model.Account;
import reactor.core.publisher.Mono;

public interface CustomerServiceClientPort {

    Mono<CustomerDto> fetchCustomerData(Account account);
}
