package com.hanaro.prayerservice.dto;

import com.hanaro.prayerservice.domain.RegisteredAccount;

import java.time.Instant;

/**
 * 등록 계좌 응답. BLESS_SPEC §5-1-5, contracts/bless-api.md GET /apis/prayer/registered-accounts.
 * holderName·relation·bank 는 M1 에서 제외 (user-service 조인 경로 미존재).
 */
public record RegisteredAccountResponse(
        Long accountId,
        String accountNumber,
        String alias,
        Instant lastUsedAt
) {
    public static RegisteredAccountResponse from(RegisteredAccount entity) {
        return new RegisteredAccountResponse(
                entity.getRegisteredAccountId(),
                entity.getAccountNumber(),
                entity.getAlias(),
                entity.getLastUsedAt()
        );
    }
}
