package com.hanaro.userservice.client;

import com.hanaro.common.response.ApiResponse;
import com.hanaro.userservice.config.InternalFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "orgInternalClient",
        url = "${downstream.org-service}",
        configuration = InternalFeignConfig.class
)
public interface OrgInternalClient {

    @GetMapping("/internal/orgs/{orgId}/summary")
    ApiResponse getOrgSummary(@PathVariable("orgId") Long orgId);
}