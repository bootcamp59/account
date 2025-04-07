package com.bootcamp.account.controller;

import com.bootcamp.account.business.AccountService;
import com.bootcamp.account.model.dto.AccountDto;
import com.bootcamp.account.model.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @GetMapping
    public Flux<Account> findAll(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Account>> findById(@PathVariable String id){
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{id}")
    public Flux<Account> findByCustomerId(@PathVariable String id){
        return service.findByCustomerId(id);
    }

    @GetMapping("/{id}/customer/{customerId}")
    public Mono<Boolean> findByCustomerId(@PathVariable String id, @PathVariable String customerId){
        return service.findByIdAndCustomerId(id, customerId)
                .map(a -> {
                    return a != null;
                }).defaultIfEmpty(false);
    }

    @PostMapping
    public Mono<ResponseEntity<Account>> create(@RequestBody AccountDto dto){
        return service.create(dto)
            .map(savedAccount -> ResponseEntity.status(HttpStatus.CREATED).body(savedAccount));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Account>>update (@RequestBody Account account, @PathVariable String id){
        return service.update(id, account)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable String id) {
        return service.delete(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
