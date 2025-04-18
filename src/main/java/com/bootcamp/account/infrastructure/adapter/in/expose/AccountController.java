package com.bootcamp.account.infrastructure.adapter.in.expose;

import com.bootcamp.account.aplication.port.in.AccountUseCase;
import com.bootcamp.account.domain.dto.DepositDto;
import com.bootcamp.account.domain.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountUseCase usecase;

    @PostMapping
    public Mono<ResponseEntity<Account>> create(@RequestBody Account account){
        return usecase.create(account)
            .map( res -> ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(res));
    }

    @GetMapping
    public Flux<Account> findAll(){
        return usecase.findAll();
    }

    @GetMapping("/{docNumber}")
    public Flux<Account> findByDocumentNumber(@PathVariable String docNumber){
        return usecase.findByDni(docNumber);
    }

    @PostMapping("/deposit")
    public Mono<Void> deposit(@RequestBody DepositDto dto){
        return usecase.deposit(dto).then();
    }

    @PostMapping("/withdraw")
    public Mono<Void> retiro(@RequestBody DepositDto dto){
        return usecase.withdraw(dto).then();
    }
}
