package com.hanaro.orgservice.controller;

import com.hanaro.common.security.CustomUserDetails;
import com.hanaro.orgservice.domain.ReligiousOrg;
import com.hanaro.orgservice.dto.OrgMyPageResponseDTO;
import com.hanaro.orgservice.dto.OrgResponseDTO;
import com.hanaro.orgservice.repository.ReligiousOrgRepository;
import com.hanaro.orgservice.service.OrgService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/orgs")
@RequiredArgsConstructor
public class OrgInternalController {

    private final ReligiousOrgRepository religiousOrgRepository;
    private final OrgService orgService;

    @GetMapping("/{orgId}")
    public OrgResponseDTO getOrg(@PathVariable Long orgId) {
        ReligiousOrg org = religiousOrgRepository.findById(orgId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 교회입니다."));

        return OrgResponseDTO.builder()
                .orgId(org.getReligiousOrgId())
                .orgName(org.getOrgName())
                .orgType(org.getOrgType().name())
                .address(org.getAddress())
                .build();
    }

    @GetMapping("/me")
    public OrgMyPageResponseDTO getMyOrg(
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        return orgService.getMyOrg(user.getOrgId());
    }
}
