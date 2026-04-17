package com.hanaro.offeringservice.service;

import com.hanaro.offeringservice.domain.Offering;
import com.hanaro.offeringservice.domain.OfferingType;
import com.hanaro.offeringservice.dto.OfferingRequestDTO;
import com.hanaro.offeringservice.repository.OfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OfferingService {
    private final OfferingRepository offeringRepository;

    @Transactional
    public Long registerOffering(Long userId, OfferingRequestDTO request) {
        Offering offering = Offering.builder()
                .userId(userId)
                .orgId(request.getOrgId())
                .accountId(request.getAccountId())
                .offeringType(OfferingType.valueOf(request.getType()))
                .amount(request.getAmount())
                .usedPoint(request.getUsedPoint())
                .offererName("무기명".equals(request.getPersonType()) ? null : request.getName())
                .prayerContent(request.getPrayerTopic())
                .build();

        return offeringRepository.save(offering).getOfferingId();
    }
}
