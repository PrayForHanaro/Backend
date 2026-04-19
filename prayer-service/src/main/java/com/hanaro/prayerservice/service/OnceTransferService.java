package com.hanaro.prayerservice.service;

import com.hanaro.prayerservice.client.user.UserClient;
import com.hanaro.prayerservice.client.user.dto.UserGivingResponse;
import com.hanaro.prayerservice.client.user.dto.WithdrawRequest;
import com.hanaro.prayerservice.domain.OnceTransfer;
import com.hanaro.prayerservice.dto.OnceTransferRequest;
import com.hanaro.prayerservice.dto.OnceTransferResponse;
import com.hanaro.prayerservice.exception.PrayerErrorCode;
import com.hanaro.prayerservice.exception.PrayerException;
import com.hanaro.prayerservice.repository.OnceTransferRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OnceTransferService {

    private final OnceTransferRepository onceTransferRepository;
    private final UserClient userClient;

    @Transactional
    public OnceTransferResponse send(Long senderId, OnceTransferRequest request) {
        UserGivingResponse giving = userClient.getGivingInfo().getData();
        if (giving == null || giving.getAccountId() == null) {
            throw new PrayerException(PrayerErrorCode.NO_DEFAULT_ACCOUNT);
        }
        Long fromAccountId = giving.getAccountId();

        BigDecimal amount = BigDecimal.valueOf(request.getAmount());
        try {
            userClient.withdraw(fromAccountId, WithdrawRequest.builder().amount(amount).build());
        } catch (FeignException e) {
            throw new PrayerException(PrayerErrorCode.TRANSFER_FAILED);
        }

        OnceTransfer saved = onceTransferRepository.save(
                OnceTransfer.builder()
                        .senderId(senderId)
                        .fromAccountId(fromAccountId)
                        .toAccountNumber(request.getAccountNumber())
                        .amount(amount)
                        .message(request.getMessage())
                        .sentAt(Instant.now())
                        .build()
        );

        return OnceTransferResponse.builder()
                .id(saved.getOnceTransferId().toString())
                .sentAt(saved.getSentAt())
                .build();
    }
}
