package com.hanaro.prayerservice.controller;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.prayerservice.dto.PrayerMessageCreateRequest;
import com.hanaro.prayerservice.dto.PrayerMessageDeleteResponse;
import com.hanaro.prayerservice.dto.PrayerMessageListResponse;
import com.hanaro.prayerservice.dto.PrayerMessageResponse;
import com.hanaro.prayerservice.dto.PrayerMessageUpdateRequest;
import com.hanaro.prayerservice.service.PrayerSavingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apis/prayer")
@RequiredArgsConstructor
public class PrayerSavingsController {

    private final PrayerSavingsService prayerSavingsService;

    @PostMapping("/prayers/{giftId}/messages")
    public ApiResponse<PrayerMessageResponse> create(
            @RequestHeader("X-Auth-User-Id") Long userId,
            @PathVariable Long giftId,
            @RequestBody PrayerMessageCreateRequest request) {
        return ApiResponse.ok(prayerSavingsService.create(userId, giftId, request));
    }

    @GetMapping("/prayers/{giftId}/messages")
    public ApiResponse<PrayerMessageListResponse> list(
            @RequestHeader("X-Auth-User-Id") Long userId,
            @PathVariable Long giftId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(prayerSavingsService.list(userId, giftId, page, size));
    }

    @PatchMapping("/messages/{messageId}")
    public ApiResponse<PrayerMessageResponse> update(
            @RequestHeader("X-Auth-User-Id") Long userId,
            @PathVariable Long messageId,
            @RequestBody PrayerMessageUpdateRequest request) {
        return ApiResponse.ok(prayerSavingsService.update(userId, messageId, request));
    }

    @DeleteMapping("/messages/{messageId}")
    public ApiResponse<PrayerMessageDeleteResponse> delete(
            @RequestHeader("X-Auth-User-Id") Long userId,
            @PathVariable Long messageId) {
        return ApiResponse.ok(prayerSavingsService.delete(userId, messageId));
    }
}
