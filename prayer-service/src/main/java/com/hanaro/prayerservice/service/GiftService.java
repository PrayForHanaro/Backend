package com.hanaro.prayerservice.service;

import com.hanaro.prayerservice.client.user.UserClient;
import com.hanaro.prayerservice.client.user.dto.UserGivingResponse;
import com.hanaro.prayerservice.domain.Gift;
import com.hanaro.prayerservice.domain.PointType;
import com.hanaro.prayerservice.domain.SavingsProduct;
import com.hanaro.prayerservice.dto.GiftReceiverResponse;
import com.hanaro.prayerservice.dto.SavingsJoinRequest;
import com.hanaro.prayerservice.dto.SavingsJoinResponse;
import com.hanaro.prayerservice.event.PointEarnEvent;
import com.hanaro.prayerservice.exception.PrayerErrorCode;
import com.hanaro.prayerservice.exception.PrayerException;
import com.hanaro.prayerservice.kafka.PointEarnPublisher;
import com.hanaro.prayerservice.repository.GiftRepository;
import com.hanaro.prayerservice.repository.SavingsProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GiftService {

    private static final long SAVINGS_JOIN_POINT = 500L;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final GiftRepository giftRepository;
    private final SavingsProductRepository savingsProductRepository;
    private final UserClient userClient;
    private final PointEarnPublisher pointEarnPublisher;

    public List<GiftReceiverResponse> getMyReceivers(Long userId) {
        LocalDate today = LocalDate.now(KST);
        return giftRepository.findAllBySenderIdAndIsActiveTrue(userId).stream()
                .map(g -> new GiftReceiverResponse(
                        g.getReceiverId(),
                        g.getGiftReceiverType().name(),
                        g.getAmount(),
                        g.getCumulativeTotal(),
                        (int) ChronoUnit.DAYS.between(
                                g.getCreatedAt().atZone(KST).toLocalDate(), today) + 1))
                .toList();
    }

    @Transactional
    public SavingsJoinResponse join(Long senderId, SavingsJoinRequest request) {
        if (giftRepository.existsBySenderIdAndReceiverId(senderId, request.getReceiverId())) {
            throw new PrayerException(PrayerErrorCode.DUPLICATE_GIFT);
        }

        SavingsProduct product = savingsProductRepository.findByIsActiveTrue()
                .orElseThrow(() -> new PrayerException(PrayerErrorCode.SAVINGS_PRODUCT_NOT_FOUND));

        UserGivingResponse giving = userClient.getGivingInfo().getData();
        if (giving == null || giving.getAccountId() == null) {
            throw new PrayerException(PrayerErrorCode.NO_DEFAULT_ACCOUNT);
        }

        // Mock 적금 계좌 ID — BLESS_SPEC §10-11 (실제 은행 계좌 개설은 Mock)
        long toSavingsAccountId = System.currentTimeMillis();

        Gift gift = Gift.builder()
                .senderId(senderId)
                .receiverId(request.getReceiverId())
                .giftReceiverType(request.getGiftReceiverType())
                .fromAccountId(giving.getAccountId())
                .toSavingsAccountId(toSavingsAccountId)
                .amount(request.getAmount())
                .transferDay(request.getTransferDay())
                .goalDays(request.getGoalDays())
                .cumulativeTotal(BigDecimal.ZERO)
                .savingsProductId(product.getSavingsProductId())
                .savingsProductName(product.getName())
                .interestRate(product.getInterestRate())
                .build();
        Gift saved = giftRepository.save(gift);

        pointEarnPublisher.publish(PointEarnEvent.builder()
                .userId(senderId)
                .pointType(PointType.SAVINGS_JOIN)
                .amount(SAVINGS_JOIN_POINT)
                .refId(saved.getGiftId())
                .info("적금가입: " + product.getName())
                .timestamp(Instant.now())
                .build());

        return SavingsJoinResponse.builder()
                .giftId(saved.getGiftId())
                .startDate(saved.getCreatedAt().toLocalDate())
                .earnedPoint(SAVINGS_JOIN_POINT)
                .build();
    }
}
