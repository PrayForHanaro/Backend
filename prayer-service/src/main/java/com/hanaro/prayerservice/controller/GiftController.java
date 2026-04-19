package com.hanaro.prayerservice.controller;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.common.security.CustomUserDetails;
import com.hanaro.prayerservice.dto.GiftReceiverResponse;
import com.hanaro.prayerservice.service.GiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gifts")
@RequiredArgsConstructor
public class GiftController {

    private final GiftService giftService;

    @GetMapping("/myReceivers")
    public ApiResponse<List<GiftReceiverResponse>> getMyReceivers(@AuthenticationPrincipal CustomUserDetails user) {
        return ApiResponse.ok(giftService.getMyReceivers(user.getUserId()));
    }
}
