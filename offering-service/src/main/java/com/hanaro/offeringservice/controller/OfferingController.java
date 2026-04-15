package com.hanaro.offeringservice.controller;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.common.security.CustomUserDetails;
import com.hanaro.offeringservice.dto.OfferingRequest;
import com.hanaro.offeringservice.service.OfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/offerings")
@RequiredArgsConstructor
public class OfferingController {

    private final OfferingService offeringService;

    @PostMapping("/once")
    public ApiResponse<Long> createOffering(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody OfferingRequest request) {
        
        Long id = offeringService.registerOffering(user.getUserId(), request);
        return ApiResponse.ok(id);
    }
}
