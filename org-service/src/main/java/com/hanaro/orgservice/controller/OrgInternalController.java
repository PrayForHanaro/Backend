package com.hanaro.orgservice.controller;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.common.security.CustomUserDetails;
import com.hanaro.orgservice.dto.OrgMyPageResponseDTO;
import com.hanaro.orgservice.service.OrgService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/orgs")
@RequiredArgsConstructor
public class OrgInternalController {

    private final OrgService orgService;

    @GetMapping("/me")
    public OrgMyPageResponseDTO getMyOrg(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return orgService.getMyOrg(user.getOrgId());
    }

    @GetMapping("/{orgId}/summary")
    public ApiResponse getOrgSummaryInternal(@PathVariable Long orgId) {
        return ApiResponse.ok(orgService.getSummary(orgId));
    }
}