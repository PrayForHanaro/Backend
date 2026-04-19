package com.hanaro.prayerservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrayerSwaggerConfig {
  @Bean
  public OpenAPI orgOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Prayer Service API")
            .version("v1.0"));
  }
}
