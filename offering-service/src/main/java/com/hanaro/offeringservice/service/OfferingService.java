package com.hanaro.offeringservice.service;

import com.hanaro.offeringservice.client.user.AccountClient;
import com.hanaro.offeringservice.client.user.UserClient;
import com.hanaro.offeringservice.domain.Offering;
import com.hanaro.offeringservice.domain.OfferingType;
import com.hanaro.offeringservice.dto.AccountWithdrawRequest;
import com.hanaro.offeringservice.dto.OfferingRequestDTO;
import com.hanaro.offeringservice.dto.UsePointRequest;
import com.hanaro.offeringservice.dto.event.OfferingEvent;
import com.hanaro.offeringservice.repository.OfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class OfferingService {
    private final OfferingRepository offeringRepository;
    private final UserClient userClient;
    private final AccountClient accountClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Transactional
    public Long registerOffering(Long userId, OfferingRequestDTO request) {
        // OfferingType 안전하게 변환
        OfferingType type = Arrays.stream(OfferingType.values())
                .filter(o -> o.name().equals(request.getType()))
                .findFirst()
                .orElse(OfferingType.기타);

        // 1. 계좌 잔액 차감 (Feign - 동기 방식)
        // 잔액 부족 시 여기서 RuntimeException 발생 -> 트랜잭션 롤백
        accountClient.withdraw(request.getAccountId(),
                new AccountWithdrawRequest(request.getAmount()));

        Offering offering = Offering.builder()
                .userId(userId)
                .orgId(request.getOrgId())
                .accountId(request.getAccountId())
                .offeringType(type)
                .amount(request.getAmount())
                .usedPoint(request.getUsedPoint())
                .offererName("무기명".equals(request.getPersonType()) ? null : request.getName())
                .prayerContent(request.getPrayerTopic())
                .build();

        Long offeringId = offeringRepository.save(offering).getOfferingId();

        // 2. 사용한 포인트가 있다면 user-service에 포인트 차감 요청 (Feign)
        if (request.getUsedPoint().intValue() > 0) {
            userClient.usePoint(userId, new UsePointRequest(request.getUsedPoint().intValue()));
        }

        // 3. Kafka 이벤트 발행 (포인트 적립 및 교회 총액 업데이트용)
        kafkaTemplate.send("offering-topic", OfferingEvent.builder()
                .userId(userId)
                .orgId(request.getOrgId())
                .amount(request.getAmount())
                .usedPoint(request.getUsedPoint().intValue())
                .offeringType(type.name())
                .build());

        return offeringId;
    }
}
