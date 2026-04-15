package com.hanaro.offeringservice.service;

import com.hanaro.offeringservice.domain.Offering;
import com.hanaro.offeringservice.domain.OfferingType;
import com.hanaro.offeringservice.dto.OfferingRequest;
import com.hanaro.offeringservice.repository.OfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OfferingService {
    private final OfferingRepository offeringRepository;

    @Transactional
    public Long registerOffering(Long userId, OfferingRequest request) {
        Offering offering = Offering.builder()
                .userId(userId)
                .orgId(request.orgId())
                .accountId(request.accountId())
                .offeringType(OfferingType.valueOf(request.type()))
                .amount(request.amount())
                .offererName("무기명".equals(request.personType()) ? null : request.name())
                .prayerContent(request.prayerTopic())
                .build();

        return offeringRepository.save(offering).getOfferingId();
    }
}
