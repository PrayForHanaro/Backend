package com.hanaro.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class InternalRequestSigner {

    private static final String ALGORITHM = "HmacSHA256";

    public String sign(
            String secret,
            String method,
            String path,
            String timestamp,
            String userId,
            String userRole,
            String orgId,
            String fromGateway,
            String callerService,
            String apiKey
    ) {
        try {
            String payload = String.join(
                    "\n",
                    normalize(method).toUpperCase(),
                    normalize(path),
                    normalize(timestamp),
                    normalize(userId),
                    normalize(userRole),
                    normalize(orgId),
                    normalize(fromGateway),
                    normalize(callerService),
                    normalize(apiKey)
            );

            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM));

            byte[] result = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(result);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to sign internal request.", exception);
        }
    }

    public boolean verify(
            String secret,
            String method,
            String path,
            String timestamp,
            String userId,
            String userRole,
            String orgId,
            String fromGateway,
            String callerService,
            String apiKey,
            String signature
    ) {
        String expected = sign(
                secret,
                method,
                path,
                timestamp,
                userId,
                userRole,
                orgId,
                fromGateway,
                callerService,
                apiKey
        );

        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                normalize(signature).getBytes(StandardCharsets.UTF_8)
        );
    }

    public boolean isTimestampValid(String timestamp, long allowedDriftSeconds) {
        if (!StringUtils.hasText(timestamp)) {
            return false;
        }

        try {
            long requestEpochSeconds = Long.parseLong(timestamp);
            long nowEpochSeconds = Instant.now().getEpochSecond();

            return Math.abs(nowEpochSeconds - requestEpochSeconds) <= allowedDriftSeconds;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : "";
    }
}