package com.hanaro.common.security;

public final class InternalSecurityHeaders {

    private InternalSecurityHeaders() {
    }

    public static final String X_AUTH_USER_ID = "X-Auth-User-Id";
    public static final String X_AUTH_USER_NAME = "X-Auth-User-Name";
    public static final String X_AUTH_USER_ROLE = "X-Auth-User-Role";
    public static final String X_AUTH_ORG_ID = "X-Auth-Org-Id";

    public static final String X_FROM_GATEWAY = "X-From-Gateway";
    public static final String X_CALLER_SERVICE = "X-Caller-Service";
    public static final String X_INTERNAL_API_KEY = "X-Internal-Api-Key";
    public static final String X_INTERNAL_TIMESTAMP = "X-Internal-Timestamp";
    public static final String X_INTERNAL_SIGNATURE = "X-Internal-Signature";
}