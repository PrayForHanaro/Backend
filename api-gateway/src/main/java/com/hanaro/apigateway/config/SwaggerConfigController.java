package com.hanaro.apigateway.config;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwaggerConfigController {

  @GetMapping("/v3/api-docs/swagger-config")
  public Map<String, Object> config() {
    return Map.of(
        "urls", List.of(
            Map.of("name", "org-service", "url", "/org/v3/api-docs"),
            Map.of("name", "offering-service", "url", "/offering/v3/api-docs"),
            Map.of("name", "activity-service", "url", "/activity/v3/api-docs"),
            Map.of("name", "prayer-service", "url", "/prayer/v3/api-docs"),
            Map.of("name", "user-service", "url", "/user/v3/api-docs")
        )
    );
  }
}
