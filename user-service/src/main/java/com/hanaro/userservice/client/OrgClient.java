package com.hanaro.userservice.client;

import com.hanaro.userservice.config.InternalFeignConfig;
import com.hanaro.userservice.dto.response.OrgMyPageResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "org-service",
        url = "${downstream.org-service}",
        configuration = InternalFeignConfig.class
)
public interface OrgClient {

  @GetMapping("/internal/orgs/me")
  OrgMyPageResponseDTO getOrg();
}