package com.hanaro.offeringservice.service;

import com.hanaro.offeringservice.dto.OfferingRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferingServiceTest {

  @Mock
  KafkaTemplate<String, Object> kafkaTemplate;

  @InjectMocks
  OfferingService offeringService;

  @Test
  void kafka_send_called() {

    OfferingRequestDTO request = new OfferingRequestDTO();

    offeringService.registerOffering(1L, request);

    verify(kafkaTemplate, times(1))
        .send(eq("offering-topic"), any());
  }
}
