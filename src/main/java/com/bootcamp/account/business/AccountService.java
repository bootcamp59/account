package com.bootcamp.account.business;

import com.bootcamp.account.model.entity.Account;
import com.bootcamp.account.model.entity.Customer;
import com.bootcamp.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final WebClient.Builder webClientBuilder;

    public Flux<Account> findAll() {
        return accountRepository.findAll();
    }

    public Mono<Account> findById(String id) {
        return accountRepository.findById(id);
    }

    public Flux<Account> findByCustomerId(String customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    public Mono<Account> create(Account account) {
        return validateCustomerType(account.getCustomerId(), account.getType())
            .then(validateAccountLimits(account.getCustomerId(), account.getType()))
            .then(accountRepository.existsByAccountNumber(account.getAccountNumber()))
            .flatMap(exists -> exists
                ? Mono.error(new IllegalArgumentException("Account number already exists"))
                : saveNewAccount(account));
    }

    private Mono<Void> validateCustomerType(String customerId, Account.AccountType accountType) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8085/api/v/customer/{id}", customerId)
                .retrieve()
                .bodyToMono(Customer.class)
                .flatMap(customer -> {
                    if (customer.getType() == Customer.CustomerType.BUSINESS &&
                            (accountType == Account.AccountType.AHORRO || accountType == Account.AccountType.PLAZO_FIJO)) {
                        return Mono.error(new IllegalArgumentException(
                                "Business customers cannot have savings or fixed-term accounts"));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validateAccountLimits(String customerId, Account.AccountType accountType) {
        if (accountType == Account.AccountType.AHORRO || accountType == Account.AccountType.CUENTA_CORRIENTE) {
            return accountRepository.findByCustomerIdAndType(customerId, accountType)
                    .count()
                    .flatMap(count -> {
                        if (count > 0) {
                            return Mono.error(new IllegalArgumentException(
                                    "Customer already has an account of this type"));
                        }
                        return Mono.empty();
                    });
        }
        return Mono.empty();
    }

    private Mono<Account> saveNewAccount(Account account) {
        account.setOpeningDate(LocalDateTime.now());
        account.setLastTransactionDate(LocalDateTime.now());
        return accountRepository.save(account);
    }

    public Mono<Account> update(String id, Account account) {
        return accountRepository.findById(id)
                .flatMap(existingAccount -> {
                    existingAccount.setBalance(account.getBalance());
                    existingAccount.setMaintenanceFee(account.getMaintenanceFee());
                    existingAccount.setMonthlyTransactionLimit(account.getMonthlyTransactionLimit());
                    existingAccount.setAllowedDayOfMonth(account.getAllowedDayOfMonth());
                    existingAccount.setHolders(account.getHolders());
                    existingAccount.setAuthorizedSigners(account.getAuthorizedSigners());
                    return accountRepository.save(existingAccount);
                });
    }

    public Mono<Void> delete(String id) {
        return accountRepository.deleteById(id);
    }
}
