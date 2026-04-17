package com.hanaro.offeringservice.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/prayers/debug")
public class DebugHeaderController {

    @GetMapping("/headers")
    public Map<String, String> debugHeaders(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Name", required = false) String userName,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestHeader(value = "X-Org-Id", required = false) String orgId
    ) {
        return Map.of(
                "X-User-Id", userId == null ? "null" : userId,
                "X-User-Name", userName == null ? "null" : userName,
                "X-User-Role", userRole == null ? "null" : userRole,
                "X-Org-Id", orgId == null ? "null" : orgId
        );
    }
}