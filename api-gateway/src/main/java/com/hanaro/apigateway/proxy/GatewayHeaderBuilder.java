package com.hanaro.apigateway.proxy;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class GatewayHeaderBuilder {

    public HttpHeaders buildInternalHeaders(Authentication authentication) {
        HttpHeaders headers = new HttpHeaders();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            return headers;
        }

        Jwt jwt = jwtAuth.getToken();

        String userId = jwt.getClaimAsString("userId");
        String userName = jwt.getClaimAsString("name");
        String orgId = jwt.getClaimAsString("orgID");
        List<String> roles = jwt.getClaimAsStringList("roles");

        // X-User-Name 헤더에 넣을 때
        String encodedName = Base64.getEncoder().encodeToString(
                userName.getBytes(StandardCharsets.UTF_8)
        );

        headers.set("X-Auth-User-Id", userId);
        headers.set("X-Auth-User-Name", encodedName != null ? encodedName : "");
        headers.set("X-Auth-Org-Id", orgId != null ? orgId : "");
        headers.set("X-Auth-User-Role", String.join(",", roles != null ? roles : Collections.emptyList()));

        return headers;
    }

    public void removeSpoofableHeaders(HttpHeaders headers) {
        headers.remove("X-Auth-User-Id");
        headers.remove("X-Auth-User-Name");
        headers.remove("X-Auth-User-Role");
        headers.remove("X-Auth-Org-Id");
    }
}