package com.hanaro.common.security;

import com.hanaro.common.auth.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("accessGuard")
public class AccessGuard {

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails;
    }

    public boolean hasRole(String roleName) {
        if (!isAuthenticated()) {
            return false;
        }

        UserRole currentRole = CurrentUser.role();

        return currentRole.name().equals(roleName);
    }

    public boolean isAdmin() {
        return isAuthenticated() && CurrentUser.isAdmin();
    }

    public boolean isManager() {
        return isAuthenticated() && CurrentUser.isManager();
    }

    public boolean isSelf(Long userId) {
        return isAuthenticated() && userId != null && userId.equals(CurrentUser.userId());
    }

    public boolean isSelfOrAdmin(Long userId) {
        return isSelf(userId) || isAdmin();
    }

    public boolean isSameOrg(Long orgId) {
        return isAuthenticated()
                && orgId != null
                && CurrentUser.orgId() != null
                && orgId.equals(CurrentUser.orgId());
    }

    public boolean isSameOrgOrManager(Long orgId) {
        return isSameOrg(orgId) || isManager();
    }
}