package com.hanaro.userservice.client;

import com.hanaro.userservice.dto.OrgResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "org-service", url = "http://org-service:8082")
public interface OrgServiceClient {
    @GetMapping("/internal/orgs/{orgId}")
    OrgResponse getOrg(@PathVariable Long orgId);
}
