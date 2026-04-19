package com.hanaro.prayerservice.kafka;

import com.hanaro.prayerservice.event.PointEarnEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointEarnPublisher {

    // TODO: RyuJiye 확정 후 교체 (2026-04-17-point-api-confirm.md §확인·요청 1)
    private static final String TOPIC = "point.earn";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(PointEarnEvent event) {
        kafkaTemplate.send(TOPIC, String.valueOf(event.getUserId()), event);
    }
}
