package com.hanaro.prayerservice.service;

import com.hanaro.prayerservice.client.user.UserClient;
import com.hanaro.prayerservice.client.user.dto.UserGivingResponse;
import com.hanaro.prayerservice.client.user.dto.WithdrawRequest;
import com.hanaro.prayerservice.domain.OnceTransfer;
import com.hanaro.prayerservice.domain.TransferStatus;
import com.hanaro.prayerservice.dto.OnceTransferRequest;
import com.hanaro.prayerservice.dto.OnceTransferResponse;
import com.hanaro.prayerservice.exception.PrayerErrorCode;
import com.hanaro.prayerservice.exception.PrayerException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnceTransferService {

    private static final int FAILURE_REASON_MAX = 255;

    private final UserClient userClient;
    private final OnceTransferStatusWriter statusWriter;

    public OnceTransferResponse send(Long senderId, OnceTransferRequest request) {
        UserGivingResponse giving = userClient.getGivingInfo().getData();
        if (giving == null || giving.getAccountId() == null) {
            throw new PrayerException(PrayerErrorCode.NO_DEFAULT_ACCOUNT);
        }
        Long fromAccountId = giving.getAccountId();
        BigDecimal amount = BigDecimal.valueOf(request.getAmount());

        OnceTransfer saved = statusWriter.createPending(OnceTransfer.builder()
                .senderId(senderId)
                .fromAccountId(fromAccountId)
                .toAccountNumber(request.getAccountNumber())
                .amount(amount)
                .message(request.getMessage())
                .sentAt(Instant.now())
                .status(TransferStatus.PENDING)
                .build());
        Long id = saved.getOnceTransferId();

        try {
            userClient.withdraw(fromAccountId, WithdrawRequest.builder().amount(amount).build());
        } catch (FeignException e) {
            String reason = truncate("Feign " + e.status() + ": " + e.getMessage());
            statusWriter.markFailed(id, reason);
            log.warn("[OnceTransfer] FAILED id={} reason={}", id, reason);
            throw new PrayerException(PrayerErrorCode.TRANSFER_FAILED);
        } catch (Exception e) {
            String reason = truncate(e.getClass().getSimpleName() + ": " + e.getMessage());
            statusWriter.markFailed(id, reason);
            log.warn("[OnceTransfer] FAILED id={} reason={}", id, reason);
            throw new PrayerException(PrayerErrorCode.TRANSFER_FAILED);
        }

        Instant completedAt = Instant.now();
        statusWriter.markSuccess(id, completedAt);

        return OnceTransferResponse.builder()
                .id(String.valueOf(id))
                .sentAt(saved.getSentAt())
                .status(TransferStatus.SUCCESS.name())
                .build();
    }

    private static String truncate(String s) {
        if (s == null) return null;
        return s.length() <= FAILURE_REASON_MAX ? s : s.substring(0, FAILURE_REASON_MAX);
    }
}
