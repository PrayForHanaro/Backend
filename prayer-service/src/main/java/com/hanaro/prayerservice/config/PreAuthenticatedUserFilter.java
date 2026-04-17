package com.hanaro.prayerservice.config;

// ================================================================
// TEMP (2026-04-17): PR #11 머지로 common/security/PreAuthenticatedUserFilter가
// 삭제된 상태. 게이트웨이(Jeongsu)가 JWT 검증 후 X-User-* 헤더를 주입하는 공식
// 경로가 확정되기 전까지, 로컬 테스트 시 curl/Postman이 직접 헤더를 넣어 호출할
// 수 있도록 이 필터가 SecurityContext에 CustomUserDetails를 주입한다.
// 팀 공식 구조 확정 후 교체·삭제 대상.
// ================================================================

import com.hanaro.common.auth.UserRole;
import com.hanaro.common.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class PreAuthenticatedUserFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String userIdStr = request.getHeader("X-User-Id");

        if (userIdStr != null && !userIdStr.isBlank()) {
            Long userId = Long.valueOf(userIdStr);

            String orgIdStr = request.getHeader("X-User-OrgId");
            Long orgId = (orgIdStr != null && !orgIdStr.isBlank()) ? Long.valueOf(orgIdStr) : null;

            String name = request.getHeader("X-User-Name");

            String roleStr = request.getHeader("X-User-Role");
            UserRole role = (roleStr != null && !roleStr.isBlank()) ? UserRole.valueOf(roleStr) : UserRole.USER;

            CustomUserDetails userDetails = new CustomUserDetails(userId, orgId, name, role);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
