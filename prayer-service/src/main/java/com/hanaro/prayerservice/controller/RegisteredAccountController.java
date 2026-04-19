package com.hanaro.prayerservice.controller;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.common.security.CustomUserDetails;
import com.hanaro.prayerservice.dto.RegisteredAccountResponse;
import com.hanaro.prayerservice.service.RegisteredAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/apis/prayer/registered-accounts")
@RequiredArgsConstructor
public class RegisteredAccountController {

    private final RegisteredAccountService service;

    @GetMapping
    public ApiResponse<List<RegisteredAccountResponse>> list(
            @AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.ok(service.getBySender(user.getUserId()));
    }
}
