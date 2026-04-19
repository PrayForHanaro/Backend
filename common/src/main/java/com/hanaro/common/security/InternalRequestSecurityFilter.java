package com.hanaro.common.security;

import com.hanaro.common.auth.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class InternalRequestSecurityFilter extends OncePerRequestFilter {

    private final InternalRequestSigner signer;
    private final String hmacSecret;
    private final Set<String> validApiKeys;
    private final Set<String> bypassExactPaths;
    private final long allowedDriftSeconds;

    public InternalRequestSecurityFilter(
            InternalRequestSigner signer,
            String hmacSecret,
            Set<String> validApiKeys,
            Set<String> bypassExactPaths,
            long allowedDriftSeconds
    ) {
        this.signer = signer;
        this.hmacSecret = hmacSecret;
        this.validApiKeys = validApiKeys;
        this.bypassExactPaths = bypassExactPaths;
        this.allowedDriftSeconds = allowedDriftSeconds;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/actuator")
                || bypassExactPaths.contains(uri);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.startsWith("/internal/")) {
            if (!validateInternalRequest(request, response)) {
                return;
            }
        } else if (path.startsWith("/apis/")) {
            if (!validateExternalRequest(request, response)) {
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden path.");
            return;
        }

        buildAuthenticationIfPresent(request);

        filterChain.doFilter(request, response);
    }

    private boolean validateExternalRequest(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String fromGateway = request.getHeader(InternalSecurityHeaders.X_FROM_GATEWAY);

        if (!"true".equals(fromGateway)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Gateway header is required.");
            return false;
        }

        return validateSignature(request, response);
    }

    private boolean validateInternalRequest(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String apiKey = request.getHeader(InternalSecurityHeaders.X_INTERNAL_API_KEY);
        String callerService = request.getHeader(InternalSecurityHeaders.X_CALLER_SERVICE);

        if (!StringUtils.hasText(apiKey) || !validApiKeys.contains(apiKey)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid internal api key.");
            return false;
        }

        if (!StringUtils.hasText(callerService)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Caller service is required.");
            return false;
        }

        return validateSignature(request, response);
    }

    private boolean validateSignature(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String timestamp = request.getHeader(InternalSecurityHeaders.X_INTERNAL_TIMESTAMP);
        String signature = request.getHeader(InternalSecurityHeaders.X_INTERNAL_SIGNATURE);

        if (!StringUtils.hasText(timestamp) || !StringUtils.hasText(signature)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing signature headers.");
            return false;
        }

        if (!signer.isTimestampValid(timestamp, allowedDriftSeconds)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired internal timestamp.");
            return false;
        }

        boolean verified = signer.verify(
                hmacSecret,
                request.getMethod(),
                request.getRequestURI(),
                timestamp,
                request.getHeader(InternalSecurityHeaders.X_AUTH_USER_ID),
                request.getHeader(InternalSecurityHeaders.X_AUTH_USER_ROLE),
                request.getHeader(InternalSecurityHeaders.X_AUTH_ORG_ID),
                request.getHeader(InternalSecurityHeaders.X_FROM_GATEWAY),
                request.getHeader(InternalSecurityHeaders.X_CALLER_SERVICE),
                request.getHeader(InternalSecurityHeaders.X_INTERNAL_API_KEY),
                signature
        );

        if (!verified) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid internal signature.");
            return false;
        }

        return true;
    }

    private void buildAuthenticationIfPresent(HttpServletRequest request) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        String userIdHeader = request.getHeader(InternalSecurityHeaders.X_AUTH_USER_ID);
        String userNameHeader = request.getHeader(InternalSecurityHeaders.X_AUTH_USER_NAME);
        String userRoleHeader = request.getHeader(InternalSecurityHeaders.X_AUTH_USER_ROLE);
        String orgIdHeader = request.getHeader(InternalSecurityHeaders.X_AUTH_ORG_ID);

        if (!StringUtils.hasText(userIdHeader) || !StringUtils.hasText(userRoleHeader)) {
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
    }
}