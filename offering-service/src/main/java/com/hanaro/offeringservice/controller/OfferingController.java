package com.hanaro.offeringservice.controller;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.common.security.CustomUserDetails;
import com.hanaro.offeringservice.dto.OfferingRequestDTO;
import com.hanaro.offeringservice.service.OfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apis/offering/offerings")
@RequiredArgsConstructor
public class OfferingController {
    private final OfferingService offeringService;

    @PostMapping("/once")
    public ApiResponse<Long> registerOffering(
            @RequestHeader("X-Auth-User-Id") Long userId,
            @RequestBody OfferingRequestDTO request) {
        return ApiResponse.ok(offeringService.registerOffering(userId, request));
    }
}
