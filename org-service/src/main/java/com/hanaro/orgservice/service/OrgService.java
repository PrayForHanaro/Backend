package com.hanaro.orgservice.service;

import com.hanaro.orgservice.domain.ReligiousOrg;
import com.hanaro.orgservice.dto.OrgSummaryResponse;
import com.hanaro.orgservice.repository.ReligiousOrgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrgService {
    private final ReligiousOrgRepository religiousOrgRepository;

    public OrgSummaryResponse getSummary(Long orgId) {
        ReligiousOrg org = religiousOrgRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        return new OrgSummaryResponse(org.getOrgName(), org.getTotalOfferingAmount());
    }

    /** 교회 누적 헌금액 업데이트 */
    @Transactional
    public void updateOfferingAmount(Long orgId, BigDecimal amount) {
        ReligiousOrg org = religiousOrgRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        org.addOfferingAmount(amount);
    }
}
