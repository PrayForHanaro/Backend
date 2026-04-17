package com.hanaro.apigateway.filter;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.servlet.function.ServerRequest;

@Configuration
public class GatewayUserHeaderFilter {

    @Bean
    public Function<ServerRequest, ServerRequest> addInternalUserHeaders() {
        return request -> {
            ServerRequest.Builder builder = ServerRequest.from(request);

            // 1) spoofing 가능한 외부 헤더 제거
            builder.headers(headers -> {
                headers.remove("X-User-Id");
                headers.remove("X-User-Name");
                headers.remove("X-User-Role");
                headers.remove("X-Org-Id");
            });

            // 2) SecurityContext에서 현재 인증 정보 꺼내기
            Authentication authentication =
                    org.springframework.security.core.context.SecurityContextHolder
                            .getContext()
                            .getAuthentication();

            if (!(authentication instanceof JwtAuthenticationToken jwtAuth)
                    || !authentication.isAuthenticated()) {
                return builder.build();
            }

            var jwt = jwtAuth.getToken();

            String userId = jwt.getSubject();
            String userName = jwt.getClaimAsString("name");
            String orgId = jwt.getClaimAsString("org_id");

            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                    .collect(Collectors.toList());

            // 3) 내부 헤더 재주입
            builder.headers(headers -> {
                if (userId != null) {
                    headers.set("X-User-Id", userId);
                }
                if (userName != null) {
                    String encodedName = Base64.getEncoder().encodeToString(
                            userName.getBytes(StandardCharsets.UTF_8)
                    );
                    headers.set("X-User-Name", encodedName);
                }
                if (orgId != null) {
                    headers.set("X-Org-Id", orgId);
                }
                if (!roles.isEmpty()) {
                    headers.set("X-User-Role", String.join(",", roles));
                }
            });

            return builder.build();
        };
    }
}