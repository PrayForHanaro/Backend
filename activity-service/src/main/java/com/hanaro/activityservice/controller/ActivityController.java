package com.hanaro.activityservice.controller;

import com.hanaro.activityservice.dto.request.ActivityRequest;
import com.hanaro.activityservice.dto.response.ActivityResponse;
import com.hanaro.activityservice.service.ActivityService;
import com.hanaro.common.response.ApiResponse;
import com.hanaro.common.security.CustomUserDetails;
import com.hanaro.common.storage.StorageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/apis/activity/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final StorageService storageService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ApiResponse<List<ActivityResponse.Summary>> getActivities(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return ApiResponse.ok(
                activityService.getActivities(
                        user.getUserId(),
                        user.getOrgId(),
                        category,
                        keyword
                )
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{activityId}")
    public ApiResponse<ActivityResponse.Detail> getActivity(
            @PathVariable("activityId") Long activityId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ApiResponse.ok(activityService.getActivity(activityId, user.getUserId()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLERGY')")
    @PostMapping(consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ActivityResponse.Detail> createActivity(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestPart("request") ActivityRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        return ApiResponse.ok(
                "활동 등록이 완료되었습니다.",
                activityService.createActivity(
                        user.getUserId(),
                        user.getOrgId(),
                        request,
                        files
                )
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{activityId}/apply")
    public ApiResponse<ActivityResponse.Detail> applyActivity(
            @PathVariable("activityId") Long activityId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ApiResponse.ok(
                "활동 참여가 완료되었습니다.",
                activityService.applyActivity(activityId, user.getUserId())
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLERGY')")
    @PostMapping("/upload")
    public ApiResponse<String> uploadPhoto(
            @RequestPart("file") MultipartFile file
    ) {
        String key = storageService.upload(file, "activity");
        return ApiResponse.ok(key);
    }
}