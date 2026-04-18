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
import com.hanaro.common.exception.BaseException;
import com.hanaro.offeringservice.exception.OfferingErrorCode;
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
            throw new BaseException(OfferingErrorCode.WITHDRAWAL_FAILED);
        }

        // 3. 사용한 포인트 차감 (Feign)
        if (request.getUsedPoint() != null && request.getUsedPoint().intValue() > 0) {
            try {
                userClient.usePoint(userId, new UsePointRequest(request.getUsedPoint().intValue()));
            } catch (Exception e) {
                // [보상 트랜잭션] 포인트 사용 실패 시, 출금 취소 이벤트 발행
                kafkaTemplate.send("withdraw-compensate-topic", OfferingEvent.builder()
                        .userId(userId)
                        .amount(request.getAmount())
                        .accountId(request.getAccountId())
                        .build());
                throw new BaseException(OfferingErrorCode.POINT_USE_FAILED);
            }
        }

        // 4. Kafka 이벤트 발행 (비동기)
        kafkaTemplate.send("offering-topic", OfferingEvent.builder()
                .userId(userId)
                .orgId(request.getOrgId())
                .accountId(request.getAccountId())
                .amount(request.getAmount())
                .usedPoint(request.getUsedPoint() != null ? request.getUsedPoint().intValue() : 0)
                .offeringType(type.name())
                .build());

        return offeringId;
    }
}

