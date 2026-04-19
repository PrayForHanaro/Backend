package com.hanaro.apigateway.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CookieTokenResolver implements BearerTokenResolver {

    @Value("${jwt.cookie-name:accessToken}")
    private String cookieName;

    @Override
    public String resolve(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (!cookieName.equals(cookie.getName())) {
                continue;
            }

            String token = cookie.getValue();

            if (!StringUtils.hasText(token)) {
                return null;
            }

            return token;
        }

        return null;
    }
}