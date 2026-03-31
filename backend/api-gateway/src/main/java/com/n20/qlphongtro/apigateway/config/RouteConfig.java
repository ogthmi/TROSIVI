package com.n20.qlphongtro.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-route", r -> r.path("/api/auth/**")
                        .uri("lb://auth-service"))

                .route("user-route", r -> r.path("/api/user/**")
                        .uri("lb://user-service"))

                .route("customer-route", r -> r.path("/api/customer/**")
                        .uri("lb://user-service"))

                .route("manager-route", r -> r.path("/api/manager/**")
                        .uri("lb://user-service"))

                .route("admin-route", r -> r.path("/api/admin/**")
                        .uri("lb://user-service"))

                .build();
    }
}
