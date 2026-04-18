package com.hanaro.prayerservice.service;

import com.hanaro.prayerservice.client.user.UserClient;
import com.hanaro.prayerservice.client.user.dto.WithdrawRequest;
import com.hanaro.prayerservice.domain.Gift;
import com.hanaro.prayerservice.domain.GiftTransfer;
import com.hanaro.prayerservice.domain.PointType;
import com.hanaro.prayerservice.domain.TransferStatus;
import com.hanaro.prayerservice.event.PointEarnEvent;
import com.hanaro.prayerservice.kafka.PointEarnPublisher;
import com.hanaro.prayerservice.repository.GiftRepository;
import com.hanaro.prayerservice.repository.GiftTransferRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoTransferService {

    private static final int FAILURE_REASON_MAX = 255;
    private static final BigDecimal RECURRING_POINT_RATE = BigDecimal.valueOf(1, 3); // 0.001

    private final GiftRepository giftRepository;
    private final GiftTransferRepository giftTransferRepository;
    private final UserClient userClient;
    private final PointEarnPublisher pointEarnPublisher;

    public void runFor(LocalDate date) {
        int day = date.getDayOfMonth();
        boolean isLastDay = date.lengthOfMonth() == day;

        List<Gift> due = giftRepository.findDueGifts(day, isLastDay);
        log.info("[AutoTransfer] {} 대상 Gift {}건 (isLastDay={})", date, due.size(), isLastDay);

        for (Gift gift : due) {
            processOne(gift.getGiftId(), date);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOne(Long giftId, LocalDate date) {
        Gift gift = giftRepository.findById(giftId).orElse(null);
        if (gift == null || !gift.isActive()) return;

        BigDecimal amount = gift.getAmount();
        TransferStatus status;
        String reason = null;

        try {
            userClient.withdraw(gift.getFromAccountId(),
                    WithdrawRequest.builder().amount(amount).build());
            status = TransferStatus.SUCCESS;
        } catch (FeignException e) {
            status = TransferStatus.FAILED;
            reason = truncate("Feign " + e.status() + ": " + e.getMessage());
        } catch (Exception e) {
            status = TransferStatus.FAILED;
            reason = truncate(e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        giftTransferRepository.save(GiftTransfer.builder()
                .gift(gift)
                .transferDate(date)
                .amount(amount)
                .status(status)
                .failureReason(reason)
                .build());

        if (status == TransferStatus.SUCCESS) {
            gift.addCumulativeTotal(amount);
            long earnedPoint = amount.multiply(RECURRING_POINT_RATE)
                    .setScale(0, RoundingMode.FLOOR)
                    .longValue();
            pointEarnPublisher.publish(PointEarnEvent.builder()
                    .userId(gift.getSenderId())
                    .pointType(PointType.SAVINGS_RECURRING)
                    .amount(earnedPoint)
                    .refId(gift.getGiftId())
                    .info("기도적금 자동이체: " + gift.getSavingsProductName())
                    .timestamp(Instant.now())
                    .build());
            log.info("[AutoTransfer] SUCCESS giftId={} amount={} earnedPoint={}",
                    giftId, amount, earnedPoint);
        } else {
            log.warn("[AutoTransfer] FAILED giftId={} amount={} reason={}",
                    giftId, amount, reason);
        }
    }

    private String truncate(String s) {
        if (s == null) return null;
        return s.length() <= FAILURE_REASON_MAX ? s : s.substring(0, FAILURE_REASON_MAX);
    }
}
