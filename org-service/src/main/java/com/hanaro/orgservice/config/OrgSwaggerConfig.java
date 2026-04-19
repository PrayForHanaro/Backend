package com.hanaro.orgservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrgSwaggerConfig {
  @Bean
  public OpenAPI orgOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Org Service API")
            .version("v1.0"));
  }
}
