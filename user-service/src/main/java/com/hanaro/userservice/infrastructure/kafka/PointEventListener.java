package com.hanaro.userservice.infrastructure.kafka;

import com.hanaro.common.event.*;
import com.hanaro.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventListener {

  private final UserService userService;

  // =========================
  // 1. 헌금 - 일시
  // =========================
  @KafkaListener(topics = "point-topic", groupId = "user-service-group")
  public void handleOfferingOnce(OfferingOncePointEvent event) {
    log.info("OfferingOnce event received: userId={}, amount={}",
        event.getUserId(), event.getDonationAmount());

    userService.processOfferingOnce(event);
  }

  // =========================
  // 2. 헌금 - 정기
  // =========================
  @KafkaListener(topics = "point-topic", groupId = "user-service-group")
  public void handleOfferingRecurring(OfferingRecurringPointEvent event) {
    log.info("OfferingRecurring event received: userId={}",
        event.getUserId());

    userService.processOfferingRecurring(event);
  }

  // =========================
  // 3. 교회 활동
  // =========================
  @KafkaListener(topics = "point-topic", groupId = "user-service-group")
  public void handleActivityChurch(ActivityChurchPointEvent event) {
    log.info("ActivityChurch event received: userId={}, title={}",
        event.getUserId(), event.getTitle());

    userService.processActivityChurch(event);
  }

  // =========================
  // 4. 봉사 활동
  // =========================
  @KafkaListener(topics = "point-topic", groupId = "user-service-group")
  public void handleActivityVolunteer(ActivityVolunteerPointEvent event) {
    log.info("ActivityVolunteer event received: userId={}, title={}",
        event.getUserId(), event.getTitle());

    userService.processActivityVolunteer(event);
  }

  // =========================
  // 5. 적금 가입
  // =========================
  @KafkaListener(topics = "point-topic", groupId = "user-service-group")
  public void handleSavingsJoin(SavingsJoinPointEvent event) {
    log.info("SavingsJoin event received: userId={}, productName={}",
        event.getUserId(), event.getProductName());

    userService.processSavingsJoin(event);
  }

  // =========================
  // 6. 적금 자동이체
  // =========================
  @KafkaListener(topics = "point-topic", groupId = "user-service-group")
  public void handleSavingsRecurring(SavingsRecurringPointEvent event) {
    log.info("SavingsRecurring event received: userId={}, targetName={}",
        event.getUserId(), event.getTargetName());

    userService.processSavingsRecurring(event);
  }
}
