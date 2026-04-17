package com.hanaro.apigateway.proxy;

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

        String userId = jwt.getSubject();
        String userName = jwt.getClaimAsString("name");
        String orgId = jwt.getClaimAsString("org_id");
        List<String> roles = jwt.getClaimAsStringList("roles");

        headers.set("X-User-Id", userId);
        headers.set("X-User-Name", userName != null ? userName : "");
        headers.set("X-Org-Id", orgId != null ? orgId : "");
        headers.set("X-User-Role", String.join(",", roles != null ? roles : Collections.emptyList()));

        return headers;
    }

    public void removeSpoofableHeaders(HttpHeaders headers) {
        headers.remove("X-User-Id");
        headers.remove("X-User-Name");
        headers.remove("X-User-Role");
        headers.remove("X-Org-Id");
    }
}