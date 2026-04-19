package com.hanaro.apigateway.config;

import com.hanaro.apigateway.security.*;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring()
        .requestMatchers(
            "/swagger-custom.js",
            "/favicon.ico",
            "/webjars/**",
            "/static/**"
        );
  }

  @Bean
  @Order(1)
  public SecurityFilterChain excludeChain(HttpSecurity http) throws Exception {

    http
        .securityMatcher(
            "/apis/auth/**",    // 경로명(api vs apis) 통일 확인 필요
            "/api/public/**",
            "/health",
            "/error",

            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-config",

            "/org/v3/api-docs",
            "/user/v3/api-docs",
            "/prayer/v3/api-docs",
            "/offering/v3/api-docs",
            "/activity/v3/api-docs"
        )
        .csrf(csrf -> csrf.disable())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
  }
    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            CookieTokenResolver tokenResolver,
            CustomJwtAuthenticationConverter converter,
            JwtAuthenticationEntryPoint entryPoint,
            JwtAccessDeniedHandler deniedHandler
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(deniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/apis/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenResolver(tokenResolver)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(converter))
                );

        return http.build();
    }
}
