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

        // 1. 헌금 정보 빌드 및 로컬 DB 저장
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

        // 2. 계좌 잔액 차감 (Feign) - 실패 시 트랜잭션 롤백 및 에러 전파
        try {
            accountClient.withdraw(request.getAccountId(), 
                    new AccountWithdrawRequest(request.getAmount()));
        } catch (Exception e) {
            throw new RuntimeException("계좌 출금에 실패했습니다: " + e.getMessage());
        }

        // 3. 사용한 포인트 차감 (Feign)
        if (request.getUsedPoint() != null && request.getUsedPoint().intValue() > 0) {
            try {
                userClient.usePoint(userId, new UsePointRequest(request.getUsedPoint().intValue()));
            } catch (Exception e) {
                // 출금은 성공했는데 포인트 차감이 실패하는 경우 (정합성 이슈)
                // 현재는 예외만 던지지만, 추후 보상 트랜잭션 도입 필요
                throw new RuntimeException("포인트 차감에 실패했습니다: " + e.getMessage());
            }
        }

        // 4. Kafka 이벤트 발행 (비동기)
        kafkaTemplate.send("offering-topic", OfferingEvent.builder()
                .userId(userId)
                .orgId(request.getOrgId())
                .amount(request.getAmount())
                .usedPoint(request.getUsedPoint() != null ? request.getUsedPoint().intValue() : 0)
                .offeringType(type.name())
                .build());

        return offeringId;
    }
}

