package com.hanaro.apigateway.config;

import com.hanaro.apigateway.security.CookieTokenResolver;
import com.hanaro.apigateway.security.CustomJwtAuthenticationConverter;
import com.hanaro.apigateway.security.JwtAccessDeniedHandler;
import com.hanaro.apigateway.security.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
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
                        .requestMatchers(
                                "/health",
                                "/info",
                                "/actuator/health",
                                "/actuator/info",
                                "/apis/user/users/signup",
                                "/apis/auth/login",
                                "/apis/auth/refresh"
                        ).permitAll()
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