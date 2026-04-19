package com.hanaro.common.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerCommonConfig {

  @Bean
  @ConditionalOnMissingBean(OpenAPI.class)
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Hana API")
            .description("""
                Hana Microservices API Docs
                
                인증 방법
                1. 상단 ADMIN / USER 버튼 클릭
                2. 자동으로 JWT 발급 및 적용
                3. 바로 API 테스트 가능
                
                Gateway에서 JWT → 내부 헤더 변환됨
                """)
            .version("v1.0")
        )
        // JWT 공통 설정
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(new Components()
            .addSecuritySchemes("bearerAuth",
                new SecurityScheme()
                    .name("Authorization")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
            )
        );
  }
}
