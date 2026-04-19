package com.hanaro.prayerservice.service;

import com.hanaro.prayerservice.domain.OnceTransfer;
import com.hanaro.prayerservice.exception.PrayerErrorCode;
import com.hanaro.prayerservice.exception.PrayerException;
import com.hanaro.prayerservice.repository.OnceTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * OnceTransfer 상태 전이를 독립 트랜잭션(REQUIRES_NEW)으로 수행.
 * OnceTransferService.send()가 외부 호출을 트랜잭션 밖으로 빼낸 뒤,
 * 각 단계(PENDING 저장, SUCCESS/FAILED 확정)를 별 트랜잭션으로 커밋하기 위해 분리.
 * self-invocation으로 인한 @Transactional 미적용을 피하려 별도 빈으로 둠.
 */
@Component
@RequiredArgsConstructor
public class OnceTransferStatusWriter {

    private final OnceTransferRepository onceTransferRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OnceTransfer createPending(OnceTransfer draft) {
        return onceTransferRepository.save(draft);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSuccess(Long id, Instant completedAt) {
        OnceTransfer transfer = onceTransferRepository.findById(id)
                .orElseThrow(() -> new PrayerException(PrayerErrorCode.TRANSFER_FAILED));
        transfer.markSuccess(completedAt);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long id, String reason) {
        OnceTransfer transfer = onceTransferRepository.findById(id)
                .orElseThrow(() -> new PrayerException(PrayerErrorCode.TRANSFER_FAILED));
        transfer.markFailed(reason);
    }
}
