package com.hanaro.orgservice.controller;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.orgservice.domain.ReligiousOrg;
import com.hanaro.orgservice.dto.OrgMyPageResponseDTO;
import com.hanaro.orgservice.dto.OrgResponseDTO;
import com.hanaro.orgservice.repository.ReligiousOrgRepository;
import com.hanaro.orgservice.service.OrgService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/orgs")
@RequiredArgsConstructor
public class OrgInternalController {

    private final ReligiousOrgRepository religiousOrgRepository;
    private final OrgService orgService;

    @GetMapping("/{orgId}")
    public ApiResponse<OrgResponseDTO> getOrg(@PathVariable Long orgId) {
        ReligiousOrg org = religiousOrgRepository.findById(orgId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 교회입니다."));

        return ApiResponse.ok(OrgResponseDTO.builder()
                .orgId(org.getReligiousOrgId())
                .orgName(org.getOrgName())
                .orgType(org.getOrgType().name())
                .address(org.getAddress())
                .build());
    }

    @GetMapping("/me")
    public ApiResponse<OrgMyPageResponseDTO> getMyOrg(
        @RequestHeader("X-Auth-Org-Id") Long orgId
    ) {
        return ApiResponse.ok(orgService.getMyOrg(orgId));
    }
}
