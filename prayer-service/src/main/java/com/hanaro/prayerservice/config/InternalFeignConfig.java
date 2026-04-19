package com.hanaro.prayerservice.config;

import com.hanaro.common.security.InternalFeignRequestInterceptor;
import com.hanaro.common.security.InternalRequestSigner;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalFeignConfig {

    @Bean
    public RequestInterceptor internalRequestInterceptor(
            InternalRequestSigner signer,
            @Value("${security.internal.hmac-secret}") String hmacSecret,
            @Value("${security.internal.api-key}") String apiKey,
            @Value("${spring.application.name}") String serviceName
    ) {
        return new InternalFeignRequestInterceptor(
                signer,
                hmacSecret,
                apiKey,
                serviceName
        );
    }
}