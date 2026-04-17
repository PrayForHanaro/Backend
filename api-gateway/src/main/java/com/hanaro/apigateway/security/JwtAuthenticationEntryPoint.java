package com.hanaro.apigateway.security;

import jakarta.servlet.http.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        System.out.println("=== JwtAuthenticationEntryPoint ===");
        System.out.println("uri = " + request.getRequestURI());
        System.out.println("exception class = " + authException.getClass().getName());
        System.out.println("message = " + authException.getMessage());

        Throwable cause = authException.getCause();
        if (cause != null) {
            System.out.println("cause class = " + cause.getClass().getName());
            System.out.println("cause message = " + cause.getMessage());
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("""
        {
          "code": "UNAUTHORIZED",
          "message": "인증이 필요합니다"
        }
        """);
    }
}