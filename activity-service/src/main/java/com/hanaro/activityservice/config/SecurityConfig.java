package com.hanaro.activityservice.config;

import com.hanaro.common.security.InternalRequestSecurityFilter;
import com.hanaro.common.security.InternalRequestSigner;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public InternalRequestSecurityFilter internalRequestSecurityFilter(
            InternalRequestSigner signer,
            @Value("${security.internal.hmac-secret}") String hmacSecret,
            @Value("${security.internal.valid-api-keys}") List<String> validApiKeys
    ) {
        return new InternalRequestSecurityFilter(
                signer,
                hmacSecret,
                new HashSet<>(validApiKeys),
                Set.of(),
                300
        );
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            InternalRequestSecurityFilter internalRequestSecurityFilter
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()
                        .requestMatchers("/internal/**").permitAll()
                        .requestMatchers("/apis/**").authenticated()
                        .anyRequest().denyAll()
                )
                .addFilterBefore(
                        internalRequestSecurityFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}