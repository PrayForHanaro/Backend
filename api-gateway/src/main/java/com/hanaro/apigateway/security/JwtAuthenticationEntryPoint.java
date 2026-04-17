package com.hanaro.apigateway.security;

import jakarta.servlet.http.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) throws IOException {

        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("""
            {
              "code": "UNAUTHORIZED",
              "message": "인증이 필요합니다."
            }
        """);
    }
}