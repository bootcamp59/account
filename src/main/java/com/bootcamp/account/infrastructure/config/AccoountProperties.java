package com.bootcamp.account.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "account")
@Getter
@Setter
public class AccoountProperties {

    private String msTransactionApi;
    private String msCustomerApi;
}
