package com.hanaro.activityservice.controller;

import com.hanaro.activityservice.dto.request.ActivityRequest;
import com.hanaro.activityservice.dto.response.ActivityResponse;
import com.hanaro.activityservice.service.ActivityService;
import com.hanaro.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apis/activity/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityResponse.Summary>>> getActivities(
            @RequestHeader(value = "X-Auth-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-Auth-Org-Id", required = false) Long orgId,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(activityService.getActivities(userId, orgId, category, keyword))
        );
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ApiResponse<ActivityResponse.Detail>> getActivity(
            @PathVariable("activityId") Long activityId,
            @RequestHeader(value = "X-Auth-User-Id", required = false) Long userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(activityService.getActivity(activityId, userId))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ActivityResponse.Detail>> createActivity(
            @RequestHeader(value = "X-Auth-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-Auth-Org-Id", required = false) Long orgId,
            @RequestBody ActivityRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.ok("활동 등록이 완료되었습니다.", activityService.createActivity(userId, orgId, request))
        );
    }

    @PostMapping("/{activityId}/apply")
    public ResponseEntity<ApiResponse<ActivityResponse.Detail>> applyActivity(
            @PathVariable("activityId") Long activityId,
            @RequestHeader(value = "X-Auth-User-Id", required = false) Long userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("활동 참여가 완료되었습니다.", activityService.applyActivity(activityId, userId))
        );
    }
}