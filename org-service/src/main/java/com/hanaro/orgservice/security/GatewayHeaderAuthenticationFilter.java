package com.hanaro.orgservice.security;

import com.hanaro.common.auth.UserRole;
import com.hanaro.common.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class GatewayHeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_USER_ID = "X-Auth-User-Id";
    private static final String HEADER_USER_NAME = "X-Auth-User-Name";
    private static final String HEADER_USER_ROLE = "X-Auth-User-Role";
    private static final String HEADER_ORG_ID = "X-Auth-Org-Id";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String userIdHeader = request.getHeader(HEADER_USER_ID);
        String userNameHeader = request.getHeader(HEADER_USER_NAME);
        String userRoleHeader = request.getHeader(HEADER_USER_ROLE);
        String orgIdHeader = request.getHeader(HEADER_ORG_ID);

        if (!StringUtils.hasText(userIdHeader) || !StringUtils.hasText(userRoleHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Long userId = Long.valueOf(userIdHeader);
            Long orgId = StringUtils.hasText(orgIdHeader) ? Long.valueOf(orgIdHeader) : null;

            String firstRole = userRoleHeader.split(",")[0].trim();
            UserRole role = UserRole.valueOf(firstRole);

            CustomUserDetails principal = new CustomUserDetails(
                    userId,
                    orgId,
                    userNameHeader,
                    role
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            principal.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ignored) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}