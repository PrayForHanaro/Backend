package com.hanaro.orgservice.controller;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.orgservice.dto.OrgSummaryResponse;
import com.hanaro.orgservice.service.OrgService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/orgs")
@RequiredArgsConstructor
public class OrgController {

    private final OrgService orgService;

    @GetMapping("/{orgId}/summary")
    public ApiResponse<OrgSummaryResponse> getSummary(@PathVariable Long orgId) {
        return ApiResponse.ok(orgService.getSummary(orgId));
    }

    /** 누적액 업데이트 API */
    public record AmountUpdateRequest(BigDecimal amount) {}

    @PutMapping("/{orgId}/offering-amount")
    public ApiResponse<Void> updateAmount(
            @PathVariable Long orgId,
            @RequestBody AmountUpdateRequest request) {
        orgService.updateOfferingAmount(orgId, request.amount());
        return ApiResponse.ok();
    }
}
