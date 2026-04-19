package com.hanaro.prayerservice.security;

import com.hanaro.common.auth.UserRole;
import com.hanaro.common.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// TEMP: 정수님(api-gateway 담당) 중앙 필터 도착 시 이 파일 삭제 + SecurityConfig의
//   - constructor 인자
//   - FilterRegistrationBean 빈
//   - addFilterBefore 1줄
// 함께 제거. 절차·근거: Docs/decisions/011-prayer-service-interim-auth-filter.md
//
// Idempotency: SecurityContext가 이미 채워진 경우 skip → 중앙 필터와 안전 공존
@Component
public class UserHeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final String HDR_USER_ID = "X-Auth-User-Id";
    private static final String HDR_USER_NAME = "X-Auth-User-Name";
    private static final String HDR_ORG_ID = "X-Auth-Org-Id";
    private static final String HDR_USER_ROLE = "X-Auth-User-Role";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String userIdHeader = request.getHeader(HDR_USER_ID);
            if (userIdHeader != null) {
                try {
                    Long userId = Long.valueOf(userIdHeader);
                    Long orgId = parseLong(request.getHeader(HDR_ORG_ID));
                    String name = request.getHeader(HDR_USER_NAME);
                    UserRole role = pickRole(request.getHeader(HDR_USER_ROLE));

                    CustomUserDetails principal = new CustomUserDetails(userId, orgId, name, role);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (NumberFormatException ignored) {
                    // gateway는 항상 숫자 user-id를 주입하므로 비숫자는 비정상.
                    // context를 비워두면 SecurityConfig가 401 반환.
                }
            }
        }
        chain.doFilter(request, response);
    }

    private static Long parseLong(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static UserRole pickRole(String header) {
        if (header == null || header.isBlank()) return UserRole.USER;
        List<String> roles = Arrays.stream(header.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        if (roles.contains(UserRole.ADMIN.name())) return UserRole.ADMIN;
        if (roles.contains(UserRole.CLERGY.name())) return UserRole.CLERGY;
        return UserRole.USER;
    }
}
