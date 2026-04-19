package com.hanaro.offeringservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OfferingSwaggerConfig {
  @Bean
  public OpenAPI orgOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Offering Service API")
            .version("v1.0"));
  }
}
