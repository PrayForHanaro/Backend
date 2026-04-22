package com.hanaro.prayerservice.infrastructure.kafka;

import com.hanaro.common.domain.PointType;
import com.hanaro.common.event.SavingsJoinPointEvent;
import com.hanaro.common.event.SavingsRecurringPointEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

//적금 가입할 때, 이체할 때
@Component
@RequiredArgsConstructor
public class PointEventPublisher {

    private static final String TOPIC = "point-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishSavingJoinPoint(Long userId, String productName, String targetName) {


        SavingsJoinPointEvent event = SavingsJoinPointEvent.builder()
            .userId(userId)
            .pointType(PointType.SAVINGS_JOIN)
            .productName(productName)
            .targetName(targetName)
            .build();

        kafkaTemplate.send(TOPIC, event);
    }

    public void publishSavingRecurringPoint(Long userId, String targetName, int pointAmount) {

        SavingsRecurringPointEvent event = SavingsRecurringPointEvent.builder()
            .userId(userId)
            .pointType(PointType.SAVINGS_RECURRING)
            .targetName(targetName)
            .pointAmount(pointAmount)
            .build();

        kafkaTemplate.send(TOPIC, event);
    }
}
