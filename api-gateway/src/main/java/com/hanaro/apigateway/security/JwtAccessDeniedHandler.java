package com.hanaro.apigateway.security;

import jakarta.servlet.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException e) throws IOException {

        response.setStatus(403);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("""
            {
              "code": "FORBIDDEN",
              "message": "권한이 없습니다."
            }
        """);
        System.out.println("Access Denied: " + e.getMessage() + "");
    }
}