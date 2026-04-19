package com.hanaro.activityservice.controller;

import com.hanaro.activityservice.dto.request.ActivityRequest;
import com.hanaro.activityservice.dto.response.ActivityResponse;
import com.hanaro.activityservice.service.ActivityService;
import com.hanaro.common.response.ApiResponse;
import com.hanaro.common.security.CustomUserDetails;
import com.hanaro.common.storage.StorageService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/apis/activity/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final StorageService storageService;


    @GetMapping
    public ApiResponse<List<ActivityResponse.Summary>> getActivities(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestParam(value = "category", required = false) String category,
        @RequestParam(value = "keyword", required = false) String keyword
    ) {
        Long userId = (user != null) ? user.getUserId() : null;
        Long orgId = (user != null) ? user.getOrgId() : null;
        return ApiResponse.ok(activityService.getActivities(userId, orgId, category, keyword));
    }

    @GetMapping("/{activityId}")
    public ApiResponse<ActivityResponse.Detail> getActivity(
        @PathVariable("activityId") Long activityId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = (user != null) ? user.getUserId() : null;
        return ApiResponse.ok(activityService.getActivity(activityId, userId));
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ActivityResponse.Detail> createActivity(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestPart("request") ActivityRequest request,
        @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        return ApiResponse.ok("활동 등록이 완료되었습니다.", activityService.createActivity(user.getUserId(), user.getOrgId(), request, files));
    }

    @PostMapping("/{activityId}/apply")
    public ApiResponse<ActivityResponse.Detail> applyActivity(
        @PathVariable("activityId") Long activityId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ApiResponse.ok("활동 참여가 완료되었습니다.", activityService.applyActivity(activityId, user.getUserId()));
    }

    @PostMapping("/upload")
    public ApiResponse<String> uploadPhoto(
        @RequestPart("file") MultipartFile file
    ) {
        String key = storageService.upload(file, "activity");
        return ApiResponse.ok(key);
    }
}