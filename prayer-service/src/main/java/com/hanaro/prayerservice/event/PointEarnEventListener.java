package com.hanaro.prayerservice.event;

import com.hanaro.prayerservice.kafka.PointEarnPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEarnEventListener {

    private final PointEarnPublisher pointEarnPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPointEarn(PointEarnEvent event) {
        try {
            pointEarnPublisher.publish(event);
        } catch (Exception e) {
            log.error("[PointEarn] Kafka publish failed userId={} refId={} type={} amount={}",
                    event.getUserId(), event.getRefId(), event.getPointType(), event.getAmount(), e);
        }
    }
}
