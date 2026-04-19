package com.hanaro.prayerservice.controller;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.prayerservice.dto.GiftReceiverResponse;
import com.hanaro.prayerservice.dto.SavingsJoinRequest;
import com.hanaro.prayerservice.dto.SavingsJoinResponse;
import com.hanaro.prayerservice.service.GiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/apis/prayer/prayers")
@RequiredArgsConstructor
public class GiftController {

    private final GiftService giftService;

    @GetMapping("/me")
    public ApiResponse<List<GiftReceiverResponse>> getMyReceivers(@RequestHeader("X-Auth-User-Id") Long userId) {
        return ApiResponse.ok(giftService.getMyReceivers(userId));
    }

    @PostMapping
    public ApiResponse<SavingsJoinResponse> join(
            @RequestHeader("X-Auth-User-Id") Long userId,
            @RequestBody SavingsJoinRequest request) {
        return ApiResponse.ok(giftService.join(userId, request));
    }
}
