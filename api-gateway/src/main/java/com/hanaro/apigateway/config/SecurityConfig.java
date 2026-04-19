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
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/static/**",
            "/favicon.ico",
            "/*.html",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/swagger-resources/**",
            "/org/v3/api-docs",
            "/offering/v3/api-docs",
            "/activity/v3/api-docs",
            "/prayer/v3/api-docs",
            "/user/v3/api-docs"
        );
  }

  @Bean
  @Order(1)
  public SecurityFilterChain excludeChain(HttpSecurity http) throws Exception {

    http
        .securityMatcher(
            "/api/auth/**",
            "/api/public/**",
            "/api/actuator/**",
            "/error",
            "/broadcast/**"
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
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(deniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health", "/info").permitAll()
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
