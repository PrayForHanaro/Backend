package com.hanaro.prayerservice.service;

import com.hanaro.prayerservice.domain.RegisteredAccount;
import com.hanaro.prayerservice.dto.RegisteredAccountResponse;
import com.hanaro.prayerservice.repository.RegisteredAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegisteredAccountService {

    private final RegisteredAccountRepository repository;

    /**
     * UPSERT: 있으면 lastUsedAt 갱신, 없으면 신규 삽입.
     * 호출 측 (OnceTransferService.send) 은 markSuccess 직후 try/catch 로 감싸 호출해야 함.
     * 실패가 송금 트랜잭션을 되돌리지 않도록 REQUIRED 기본 전파.
     */
    @Transactional
    public void registerOrTouch(Long senderId, String accountNumber) {
        Instant now = Instant.now();
        repository.findBySenderIdAndAccountNumber(senderId, accountNumber)
                .ifPresentOrElse(
                        existing -> existing.touch(now),
                        () -> repository.save(RegisteredAccount.builder()
                                .senderId(senderId)
                                .accountNumber(accountNumber)
                                .alias(null)
                                .lastUsedAt(now)
                                .build())
                );
    }

    @Transactional(readOnly = true)
    public List<RegisteredAccountResponse> getBySender(Long senderId) {
        return repository.findAllBySenderIdOrderByLastUsedAtDesc(senderId).stream()
                .map(RegisteredAccountResponse::from)
                .toList();
    }
}
