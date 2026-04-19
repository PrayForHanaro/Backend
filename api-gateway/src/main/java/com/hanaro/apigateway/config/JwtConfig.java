package com.hanaro.apigateway.config;

import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.audience}")
    private String audience;

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).build();

        OAuth2TokenValidator<Jwt> issuerValidator =
                JwtValidators.createDefaultWithIssuer(issuer);

        OAuth2TokenValidator<Jwt> audienceValidator =
                new JwtClaimValidator<List<String>>(
                        "aud",
                        aud -> aud != null && aud.contains(audience)
                );

        OAuth2TokenValidator<Jwt> accessTypeValidator =
                new JwtClaimValidator<String>(
                        "type",
                        "access"::equals
                );

        OAuth2TokenValidator<Jwt> validator =
                new DelegatingOAuth2TokenValidator<>(
                        issuerValidator,
                        audienceValidator,
                        accessTypeValidator
                );

        decoder.setJwtValidator(validator);

        return decoder;
    }
}