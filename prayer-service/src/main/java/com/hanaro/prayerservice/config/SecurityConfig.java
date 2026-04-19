package com.hanaro.prayerservice.config;

import com.hanaro.prayerservice.security.UserHeaderAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// TEMP: 다음 항목들은 정수님(api-gateway 담당) 중앙 필터 도착 시 제거 대상.
//   1. UserHeaderAuthenticationFilter constructor injection
//   2. FilterRegistrationBean (servlet 자동 등록 차단)
//   3. addFilterBefore(userHeaderAuthenticationFilter, ...) 줄
// 도입 사유 / 제거 절차: Docs/decisions/011-prayer-service-interim-auth-filter.md
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserHeaderAuthenticationFilter userHeaderAuthenticationFilter;

    public SecurityConfig(UserHeaderAuthenticationFilter userHeaderAuthenticationFilter) {
        this.userHeaderAuthenticationFilter = userHeaderAuthenticationFilter;
    }

    // @Component 등록된 OncePerRequestFilter는 Spring Boot가 servlet 컨테이너에 자동 등록한다.
    // Security 체인에 addFilterBefore 도 하면 같은 필터가 두 번 실행되므로
    // 아래 빈으로 servlet 자동 등록을 비활성화하고 Security 체인에서만 동작시킨다.
    @Bean
    public FilterRegistrationBean<UserHeaderAuthenticationFilter> userHeaderAuthFilterRegistration(
            UserHeaderAuthenticationFilter filter) {
        FilterRegistrationBean<UserHeaderAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(userHeaderAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
