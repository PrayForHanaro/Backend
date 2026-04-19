package com.hanaro.apigateway.config;

import com.hanaro.apigateway.filter.GatewayUserHeaderFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayRouteConfig {

    @Value("${downstream.offering-service}")
    private String offeringServiceUri;

    @Value("${downstream.activity-service}")
    private String activityServiceUri;

    @Value("${downstream.prayer-service}")
    private String prayerServiceUri;

    @Value("${downstream.user-service}")
    private String userServiceUri;

    @Value("${downstream.org-service}")
    private String orgServiceUri;

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes(
            GatewayUserHeaderFilter gatewayUserHeaderFilter
    ) {
        return GatewayRouterFunctions.route("offering-service")
                .route(path("/apis/offering/**"), http())
                .before(gatewayUserHeaderFilter.addInternalUserHeaders())
                .before(uri(offeringServiceUri))
                .build()

                .and(GatewayRouterFunctions.route("activity-service")
                        .route(path("/apis/activity/**"), http())
                        .before(gatewayUserHeaderFilter.addInternalUserHeaders())
                        .before(uri(activityServiceUri))
                        .build())

                .and(GatewayRouterFunctions.route("prayer-service")
                        .route(path("/apis/prayer/**"), http())
                        .before(gatewayUserHeaderFilter.addInternalUserHeaders())
                        .before(uri(prayerServiceUri))
                        .build())

                .and(GatewayRouterFunctions.route("user-service")
                        .route(path("/apis/user/**"), http())
                        .before(gatewayUserHeaderFilter.addInternalUserHeaders())
                        .before(uri(userServiceUri))
                        .build())

                .and(GatewayRouterFunctions.route("org-service")
                        .route(path("/apis/org/**"), http())
                        .before(gatewayUserHeaderFilter.addInternalUserHeaders())
                        .before(uri(orgServiceUri))
                        .build());
    }
}