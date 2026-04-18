package com.hanaro.userservice.service;

import com.hanaro.common.exception.BaseException;
import com.hanaro.userservice.domain.Account;
import com.hanaro.userservice.exception.UserErrorCode;
import com.hanaro.userservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public void withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BaseException(UserErrorCode.ACCOUNT_NOT_FOUND));
        account.withdraw(amount);
    }
}
