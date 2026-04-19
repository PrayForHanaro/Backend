package com.hanaro.common.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Collection;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class InternalFeignRequestInterceptor implements RequestInterceptor {

    private final InternalRequestSigner signer;
    private final String hmacSecret;
    private final String apiKey;
    private final String serviceName;

    public InternalFeignRequestInterceptor(
            InternalRequestSigner signer,
            String hmacSecret,
            String apiKey,
            String serviceName
    ) {
        this.signer = signer;
        this.hmacSecret = hmacSecret;
        this.apiKey = apiKey;
        this.serviceName = serviceName;
    }

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest currentRequest = currentRequest();

        copyHeaderIfPresent(currentRequest, template, InternalSecurityHeaders.X_AUTH_USER_ID);
        copyHeaderIfPresent(currentRequest, template, InternalSecurityHeaders.X_AUTH_USER_NAME);
        copyHeaderIfPresent(currentRequest, template, InternalSecurityHeaders.X_AUTH_USER_ROLE);
        copyHeaderIfPresent(currentRequest, template, InternalSecurityHeaders.X_AUTH_ORG_ID);

        template.header(InternalSecurityHeaders.X_INTERNAL_API_KEY, apiKey);
        template.header(InternalSecurityHeaders.X_CALLER_SERVICE, serviceName);

        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        template.header(InternalSecurityHeaders.X_INTERNAL_TIMESTAMP, timestamp);

        String path = stripQueryString(template.path());
        String userId = firstHeader(template, InternalSecurityHeaders.X_AUTH_USER_ID);
        String userRole = firstHeader(template, InternalSecurityHeaders.X_AUTH_USER_ROLE);
        String orgId = firstHeader(template, InternalSecurityHeaders.X_AUTH_ORG_ID);

        String signature = signer.sign(
                hmacSecret,
                template.method(),
                path,
                timestamp,
                userId,
                userRole,
                orgId,
                "",
                serviceName,
                apiKey
        );

        template.header(InternalSecurityHeaders.X_INTERNAL_SIGNATURE, signature);
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        return attributes != null ? attributes.getRequest() : null;
    }

    private void copyHeaderIfPresent(
            HttpServletRequest request,
            RequestTemplate template,
            String headerName
    ) {
        if (request == null) {
            return;
        }

        String value = request.getHeader(headerName);

        if (value != null && !value.isBlank()) {
            template.header(headerName, value);
        }
    }

    private String firstHeader(RequestTemplate template, String headerName) {
        Collection<String> values = template.headers().get(headerName);

        if (values == null || values.isEmpty()) {
            return "";
        }

        return values.iterator().next();
    }

    private String stripQueryString(String path) {
        int queryStartIndex = path.indexOf('?');

        return queryStartIndex >= 0 ? path.substring(0, queryStartIndex) : path;
    }
}