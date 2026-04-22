package com.hanaro.offeringservice.infrastructure.kafka;

import com.hanaro.common.domain.OfferingType;
import com.hanaro.common.domain.PointType;
import com.hanaro.common.event.OfferingOncePointEvent;
import com.hanaro.common.event.OfferingRecurringPointEvent;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

//적금 가입할 때, 이체할 때
@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventPublisher {

    private static final String TOPIC = "point-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOfferingRecurring(Long userId, BigDecimal donationAmount, OfferingType offeringType) {


        OfferingRecurringPointEvent event = OfferingRecurringPointEvent.builder()
            .userId(userId)
            .pointType(PointType.OFFERING_RECURRING)
            .donationAmount(donationAmount)
            .offeringType(offeringType)
            .build();

        kafkaTemplate.send(TOPIC, event);
    }

    public void publishOfferingOnce(Long userId, BigDecimal donationAmount, OfferingType offeringType) {

        OfferingOncePointEvent event = OfferingOncePointEvent.builder()
            .userId(userId)
            .pointType(PointType.OFFERING_ONCE)
            .donationAmount(donationAmount)
            .offeringType(offeringType)
            .build();

        kafkaTemplate.send(TOPIC, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("포인트 이벤트 전송 실패", ex);
            }
        });;
    }
}
