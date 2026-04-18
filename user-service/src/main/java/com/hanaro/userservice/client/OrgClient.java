package com.hanaro.userservice.client;

import com.hanaro.userservice.dto.response.OrgMyPageResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
    name = "org-service",
    url = "http://org-service:8080")
public interface OrgClient {
  @GetMapping("/internal/orgs/me")
  OrgMyPageResponseDTO getOrg();
}
