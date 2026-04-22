package com.hanaro.activityservice.kafka;

import com.hanaro.common.domain.PointType;
import com.hanaro.common.event.ActivityChurchPointEvent;
import com.hanaro.common.event.ActivityVolunteerPointEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Componenttopic
@RequiredArgsConstructor
public class PointEventPublisher {

    private static final String TOPIC = "point-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishActivityChurch(Long userId, String title) {


        ActivityChurchPointEvent event = ActivityChurchPointEvent.builder()
            .userId(userId)
            .pointType(PointType.ACTIVITY_CHURCH)
            .title(title)
            .build();

        kafkaTemplate.send(TOPIC, event);
    }

    public void publishActivityVolunteer(Long userId, String title) {

        ActivityVolunteerPointEvent event = ActivityVolunteerPointEvent.builder()
            .userId(userId)
            .pointType(PointType.ACTIVITY_VOLUNTEER)
            .title(title)
            .build();

        kafkaTemplate.send(TOPIC, event);
    }
}
