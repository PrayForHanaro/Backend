package com.hanaro.offeringservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka-test")
public class KafkaTestController {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  @GetMapping
  public String send() {
    kafkaTemplate.send("test-topic", "hello kafka");
    return "sent";
  }
}
