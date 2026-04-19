package com.hanaro.prayerservice.controller;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.prayerservice.dto.OnceTransferRequest;
import com.hanaro.prayerservice.dto.OnceTransferResponse;
import com.hanaro.prayerservice.service.OnceTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apis/prayer/once-transfers")
@RequiredArgsConstructor
public class OnceTransferController {

    private final OnceTransferService onceTransferService;

    @PostMapping
    public ApiResponse<OnceTransferResponse> send(
            @RequestHeader("X-Auth-User-Id") Long userId,
            @Valid @RequestBody OnceTransferRequest request) {
        return ApiResponse.ok(onceTransferService.send(userId, request));
    }
}
