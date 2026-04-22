package com.hanaro.offeringservice.service;

import com.hanaro.common.domain.OfferingType;
import com.hanaro.offeringservice.domain.Offering;
import com.hanaro.offeringservice.dto.OfferingRequestDTO;
import com.hanaro.offeringservice.repository.OfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OfferingTransactionalHelper {
    private final OfferingRepository offeringRepository;

    @Transactional
    public Long saveOffering(Long userId, OfferingRequestDTO request, OfferingType type) {
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
}
