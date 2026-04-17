package com.hanaro.userservice.consumer;

import com.hanaro.userservice.dto.event.PointEvent;
import com.hanaro.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserConsumer {

    private final UserService userService;

    @KafkaListener(topics = "point-topic", groupId = "user-service-group")
    public void handlePointEvent(PointEvent event) {

        log.info("포인트 이벤트 수신 type={}, userId={}",
            event.getPointType(), event.getUserId());

        try {
            userService.processPoint(event);
        } catch (Exception e) {
            log.error("포인트 처리 실패", e);
        }
    }
}
