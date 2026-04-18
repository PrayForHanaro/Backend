package com.hanaro.userservice.controller;

import com.hanaro.userservice.dto.request.AccountWithdrawRequest;
import com.hanaro.userservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/accounts")
@RequiredArgsConstructor
public class InternalAccountController {
    private final AccountService accountService;

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<Void> withdraw(
            @PathVariable Long accountId,
            @RequestBody AccountWithdrawRequest request) {
        accountService.withdraw(accountId, request.getAmount());
        return ResponseEntity.ok().build();
    }
}
