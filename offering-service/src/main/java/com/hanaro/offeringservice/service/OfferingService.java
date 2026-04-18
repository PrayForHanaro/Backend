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


    public Long registerOffering(Long userId, OfferingRequestDTO request) {
        // 1. 로컬 DB 저장 (최소 트랜잭션 범위)
        Long offeringId = saveOffering(userId, request);

        // 2. 외부 서비스 호출 (트랜잭션 외부 - 네트워크 I/O)
        try {
            accountClient.withdraw(request.getAccountId(), new AccountWithdrawRequest(request.getAmount()));
        } catch (Exception e) {
            // 외부 호출 실패 시: 이미 저장된 헌금 정보 삭제 (보상 트랜잭션)
            offeringRepository.deleteById(offeringId);
            throw new BaseException(OfferingErrorCode.WITHDRAWAL_FAILED);
        }

        try {
            if (request.getUsedPoint() != null && request.getUsedPoint().intValue() > 0) {
                userClient.usePoint(userId, new UsePointRequest(request.getUsedPoint().intValue()));
            }
        } catch (Exception e) {
            kafkaTemplate.send("withdraw-compensate-topic", OfferingEvent.builder()
                    .userId(userId)
                    .amount(request.getAmount())
                    .accountId(request.getAccountId())
                    .build()).get();
            throw new BaseException(OfferingErrorCode.POINT_USE_FAILED);
        }

        // 3. Kafka 이벤트 발행 (비동기, 성공 보장 필요 시 추후 Outbox 패턴 도입)
        try {
            kafkaTemplate.send("offering-topic", OfferingEvent.builder()
                    .userId(userId)
                    .orgId(request.getOrgId())
                    .accountId(request.getAccountId())
                    .amount(request.getAmount())
                    .usedPoint(request.getUsedPoint() != null ? request.getUsedPoint().intValue() : 0)
                    .offeringType(getOfferingType(request.getType()).name())
                    .build()).get();
        } catch (Exception e) {
             throw new BaseException(OfferingErrorCode.OFFERING_EVENT_PUBLISH_FAILED);
        }

        return offeringId;
    }

    @Transactional
    public Long saveOffering(Long userId, OfferingRequestDTO request) {
        OfferingType type = getOfferingType(request.getType());
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
        return offeringRepository.save(offering).getOfferingId();
    }

    private OfferingType getOfferingType(String typeName) {
        return Arrays.stream(OfferingType.values())
                .filter(o -> o.name().equals(typeName))
                .findFirst()
                .orElse(OfferingType.기타);
    }

