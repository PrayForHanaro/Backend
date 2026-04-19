package com.hanaro.apigateway.filter;

import com.hanaro.common.security.InternalRequestSigner;
import com.hanaro.common.security.InternalSecurityHeaders;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.servlet.function.ServerRequest;

@Configuration
public class GatewayUserHeaderFilter {

    private final InternalRequestSigner signer;
    private final String hmacSecret;

    public GatewayUserHeaderFilter(
            InternalRequestSigner signer,
            @Value("${security.internal.hmac-secret}") String hmacSecret
    ) {
        this.signer = signer;
        this.hmacSecret = hmacSecret;
    }

    @Bean
    public Function<ServerRequest, ServerRequest> addInternalUserHeaders() {
        return request -> {
            ServerRequest.Builder builder = ServerRequest.from(request);

            builder.headers(headers -> {
                headers.remove(InternalSecurityHeaders.X_AUTH_USER_ID);
                headers.remove(InternalSecurityHeaders.X_AUTH_USER_NAME);
                headers.remove(InternalSecurityHeaders.X_AUTH_USER_ROLE);
                headers.remove(InternalSecurityHeaders.X_AUTH_ORG_ID);
                headers.remove(InternalSecurityHeaders.X_FROM_GATEWAY);
                headers.remove(InternalSecurityHeaders.X_CALLER_SERVICE);
                headers.remove(InternalSecurityHeaders.X_INTERNAL_API_KEY);
                headers.remove(InternalSecurityHeaders.X_INTERNAL_TIMESTAMP);
                headers.remove(InternalSecurityHeaders.X_INTERNAL_SIGNATURE);
            });

            Authentication authentication =
                    org.springframework.security.core.context.SecurityContextHolder
                            .getContext()
                            .getAuthentication();

            String userId = "";
            String userName = "";
            String orgId = "";
            String userRole = "";

            if (authentication instanceof JwtAuthenticationToken jwtAuth && authentication.isAuthenticated()) {
                var jwt = jwtAuth.getToken();

                userId = jwt.getSubject();
                userName = jwt.getClaimAsString("name");
                orgId = jwt.getClaimAsString("org_id");

                List<String> roles = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                        .collect(Collectors.toList());

                userRole = String.join(",", roles);
            }

            String timestamp = String.valueOf(Instant.now().getEpochSecond());
            String fromGateway = "true";
            String callerService = "api-gateway";
            String path = request.uri().getPath();

            String signature = signer.sign(
                    hmacSecret,
                    request.method().name(),
                    path,
                    timestamp,
                    userId,
                    userRole,
                    orgId,
                    fromGateway,
                    callerService,
                    ""
            );

            String finalUserId = userId;
            String finalUserName = userName;
            String finalOrgId = orgId;
            String finalUserRole = userRole;

            builder.headers(headers -> {
                if (finalUserId != null && !finalUserId.isBlank()) {
                    headers.set(InternalSecurityHeaders.X_AUTH_USER_ID, finalUserId);
                }

                if (finalUserName != null && !finalUserName.isBlank()) {
                    headers.set(InternalSecurityHeaders.X_AUTH_USER_NAME, finalUserName);
                }

                if (finalOrgId != null && !finalOrgId.isBlank()) {
                    headers.set(InternalSecurityHeaders.X_AUTH_ORG_ID, finalOrgId);
                }

                if (finalUserRole != null && !finalUserRole.isBlank()) {
                    headers.set(InternalSecurityHeaders.X_AUTH_USER_ROLE, finalUserRole);
                }

                headers.set(InternalSecurityHeaders.X_FROM_GATEWAY, fromGateway);
                headers.set(InternalSecurityHeaders.X_CALLER_SERVICE, callerService);
                headers.set(InternalSecurityHeaders.X_INTERNAL_TIMESTAMP, timestamp);
                headers.set(InternalSecurityHeaders.X_INTERNAL_SIGNATURE, signature);
            });

            return builder.build();
        };
    }
}