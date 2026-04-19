package com.hanaro.prayerservice.controller;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.prayerservice.dto.SavingsProductCreateRequest;
import com.hanaro.prayerservice.dto.SavingsProductResponse;
import com.hanaro.prayerservice.service.SavingsProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apis/prayer/admin/savings-products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SavingsProductAdminController {

    private final SavingsProductService service;

    @PostMapping
    public ApiResponse<SavingsProductResponse> create(@Valid @RequestBody SavingsProductCreateRequest request) {
        return ApiResponse.ok(service.create(request));
    }

    @GetMapping
    public ApiResponse<List<SavingsProductResponse>> list() {
        return ApiResponse.ok(service.findAll());
    }

    @PatchMapping("/{id}/activate")
    public ApiResponse<Void> activate(@PathVariable Long id) {
        service.activate(id);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.ok();
    }
}
