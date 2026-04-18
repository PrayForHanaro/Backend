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
    private final OfferingTransactionalHelper offeringTransactionalHelper; // 추가

    public Long registerOffering(Long userId, OfferingRequestDTO request) {
        OfferingType type = getOfferingType(request.getType());

        // 1. 외부 서비스 호출 (먼저 검증)
        try {
            accountClient.withdraw(request.getAccountId(), new AccountWithdrawRequest(request.getAmount()));
        } catch (Exception e) {
            throw new BaseException(OfferingErrorCode.WITHDRAWAL_FAILED);
        }

        // 2. 로컬 DB 저장 (성공 시 offeringId 반환)
        Long offeringId = offeringTransactionalHelper.saveOffering(userId, request, type);

        // 3. 포인트 차감 (실패 시 보상 조치)
        try {
            if (request.getUsedPoint() != null && request.getUsedPoint().intValue() > 0) {
                userClient.usePoint(userId, new UsePointRequest(request.getUsedPoint().intValue()));
            }
        } catch (Exception e) {
            // [보상 조치] 포인트 실패 시 -> 로컬 DB 삭제 + 출금 취소 이벤트 발행
            offeringRepository.deleteById(offeringId);
            kafkaTemplate.send("withdraw-compensate-topic", OfferingEvent.builder()
                            .userId(userId)
                            .amount(request.getAmount())
                            .accountId(request.getAccountId())
                            .build())
                    .whenComplete((result, ex) -> {
                        if (ex != null) System.err.println("보상 이벤트 전송 실패: " + ex.getMessage());
                    });
            throw new BaseException(OfferingErrorCode.POINT_USE_FAILED);
        }

        // 4. Kafka 이벤트 발행
        kafkaTemplate.send("offering-topic", OfferingEvent.builder()
                        .userId(userId)
                        .orgId(request.getOrgId())
                        .accountId(request.getAccountId())
                        .amount(request.getAmount())
                        .usedPoint(request.getUsedPoint() != null ? request.getUsedPoint().intValue() : 0)
                        .offeringType(type.name())
                        .build())
                .whenComplete((result, ex) -> {
                    if (ex != null) System.err.println("헌금 이벤트 전송 실패: " + ex.getMessage());
                });

        return offeringId;
    }

    private OfferingType getOfferingType(String typeName) {
        return Arrays.stream(OfferingType.values())
            .filter(o -> o.name().equals(typeName))
            .findFirst()
            .orElse(OfferingType.기타);
    }
}

