package com.hanaro.prayerservice.controller;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.prayerservice.dto.SavingsProductCreateRequest;
import com.hanaro.prayerservice.dto.SavingsProductResponse;
import com.hanaro.prayerservice.exception.PrayerErrorCode;
import com.hanaro.prayerservice.exception.PrayerException;
import com.hanaro.prayerservice.service.SavingsProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

// TEMP: 서비스 내부 SecurityConfig 제거 후 api-gateway의 role 기반 라우팅 전까지
// 컨트롤러 레벨에서 자체 보호. 게이트웨이에서 처리하기 시작하면 이 가드는 제거.
@RestController
@RequestMapping("/apis/prayer/admin/savings-products")
@RequiredArgsConstructor
public class SavingsProductAdminController {

    private final SavingsProductService service;

    @PostMapping
    public ApiResponse<SavingsProductResponse> create(
            @RequestHeader("X-Auth-User-Role") String role,
            @RequestBody SavingsProductCreateRequest request) {
        requireAdmin(role);
        return ApiResponse.ok(service.create(request));
    }

    @GetMapping
    public ApiResponse<List<SavingsProductResponse>> list(@RequestHeader("X-Auth-User-Role") String role) {
        requireAdmin(role);
        return ApiResponse.ok(service.findAll());
    }

    @PatchMapping("/{id}/activate")
    public ApiResponse<Void> activate(
            @RequestHeader("X-Auth-User-Role") String role,
            @PathVariable Long id) {
        requireAdmin(role);
        service.activate(id);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @RequestHeader("X-Auth-User-Role") String role,
            @PathVariable Long id) {
        requireAdmin(role);
        service.delete(id);
        return ApiResponse.ok();
    }

    private void requireAdmin(String roleHeader) {
        if (roleHeader == null || Arrays.stream(roleHeader.split(","))
                .map(String::trim)
                .noneMatch("ADMIN"::equals)) {
            throw new PrayerException(PrayerErrorCode.ADMIN_ONLY);
        }
    }
}
