package com.hanaro.orgservice.controller;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.orgservice.service.OrgService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/orgs")
@RequiredArgsConstructor
public class OrgInternalController {

    private final OrgService orgService;

    @GetMapping("/{orgId}/summary")
    public ApiResponse getOrgSummaryInternal(@PathVariable Long orgId) {
        return ApiResponse.ok(orgService.getOrgSummary(orgId));
    }
}