package com.hanaro.userservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserSwaggerConfig {
  @Bean
  public OpenAPI orgOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("User Service API")
            .version("v1.0"));
  }
}
