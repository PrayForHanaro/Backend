package com.hanaro.offeringservice.service;

import com.hanaro.offeringservice.client.user.AccountClient;
import com.hanaro.offeringservice.client.user.UserClient;
import com.hanaro.offeringservice.domain.Offering;
import com.hanaro.offeringservice.dto.AccountWithdrawRequest;
import com.hanaro.offeringservice.dto.OfferingRequestDTO;
import com.hanaro.offeringservice.dto.UsePointRequest;
import com.hanaro.offeringservice.repository.OfferingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class OfferingServiceTest {

  @InjectMocks
  private OfferingService offeringService;

  @Mock
  private OfferingRepository offeringRepository;

  @Mock
  private UserClient userClient;

  @Mock
  private AccountClient accountClient;

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  @Test
  void 헌금_등록_성공() {
    // given
    Long userId = 1L;

    OfferingRequestDTO request = new OfferingRequestDTO(
        1L, // orgId
        10L, // accountId
        "십일조",
        "기명",
        "홍길동",
        BigDecimal.valueOf(10000),
        BigDecimal.valueOf(100),
        "기도제목"
    );

    Offering savedOffering = Offering.builder()
        .offeringId(1L)
        .build();

    when(offeringRepository.save(any(Offering.class)))
        .thenReturn(savedOffering);

    // when
    Long result = offeringService.registerOffering(userId, request);

    // then
    verify(accountClient).withdraw(
        eq(10L),
        any(AccountWithdrawRequest.class)
    );

    verify(userClient).usePoint(any(UsePointRequest.class));

    verify(offeringRepository).save(any(Offering.class));

    verify(kafkaTemplate).send(eq("offering-topic"), any());

    assert result.equals(1L);
  }
}
