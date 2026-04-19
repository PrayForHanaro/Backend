package com.hanaro.common.security;

import com.hanaro.common.auth.UserRole;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

    private CurrentUser() {
    }

    public static CustomUserDetails get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || !(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
            throw new IllegalStateException("인증된 사용자가 없습니다.");
        }

        return principal;
    }

    public static Long userId() {
        return get().getUserId();
    }

    public static Long orgId() {
        return get().getOrgId();
    }

    public static String name() {
        return get().getName();
    }

    public static UserRole role() {
        return get().getRole();
    }

    public static boolean isAdmin() {
        return role() == UserRole.ADMIN;
    }

    public static boolean isManager() {
        UserRole role = role();

        return role == UserRole.ADMIN || role == UserRole.CLERGY;
    }
}