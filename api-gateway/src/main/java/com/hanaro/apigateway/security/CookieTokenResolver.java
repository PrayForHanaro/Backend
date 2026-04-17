package com.hanaro.apigateway.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;

@Component
public class CookieTokenResolver implements BearerTokenResolver {

    @Value("${jwt.cookie-name:accessToken}")
    private String cookieName;

    @Override
    public String resolve(HttpServletRequest request) {
        System.out.println("=== CookieTokenResolver ===");
        System.out.println("cookieName = " + cookieName);
        System.out.println("uri = " + request.getRequestURI());

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            System.out.println("cookies = null");
            return null;
        }

        for (Cookie cookie : cookies) {
            System.out.println("cookie = " + cookie.getName());
            if (cookieName.equals(cookie.getName())) {
                String token = cookie.getValue();
                System.out.println("accessToken found");
                System.out.println("token preview = " + token.substring(0, Math.min(20, token.length())));
                return token;
            }
        }

        System.out.println("accessToken not found");
        return null;
    }
}