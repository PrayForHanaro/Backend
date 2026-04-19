package com.hanaro.prayerservice.controller;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.common.security.CustomUserDetails;
import com.hanaro.prayerservice.dto.OnceTransferRequest;
import com.hanaro.prayerservice.dto.OnceTransferResponse;
import com.hanaro.prayerservice.service.OnceTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apis/prayer/once-transfers")
@RequiredArgsConstructor
public class OnceTransferController {

    private final OnceTransferService onceTransferService;

    @PostMapping
    public ApiResponse<OnceTransferResponse> send(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody OnceTransferRequest request) {
        return ApiResponse.ok(onceTransferService.send(user.getUserId(), request));
    }
}
