package com.hanaro.orgservice.service;

import com.hanaro.orgservice.domain.ReligiousOrg;
import com.hanaro.orgservice.dto.OrgMyPageResponseDTO;
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
        
        // 만원 단위로 변환 (소수점 이하 버림)
        BigDecimal totalInTenThousand = org.getTotalOfferingAmount()
                .divide(new BigDecimal("10000"), 0, java.math.RoundingMode.DOWN);
                
        return new OrgSummaryResponse(org.getOrgName(), totalInTenThousand);
    }

    /** 교회 누적 헌금액 업데이트 */
    @Transactional
    public void updateOfferingAmount(Long orgId, BigDecimal amount) {
        ReligiousOrg org = religiousOrgRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        org.addOfferingAmount(amount);
    }

    public OrgMyPageResponseDTO getMyOrg(Long orgId){
        ReligiousOrg org = religiousOrgRepository
            .findById(orgId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 교회"));

        return OrgMyPageResponseDTO.from(org);
    }
}
