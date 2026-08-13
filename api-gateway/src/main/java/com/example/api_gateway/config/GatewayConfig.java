//package com.example.api_gateway.config;
//
//import com.example.api_gateway.filter.JwtAuthenticationGatewayFilterFactory;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class GatewayConfig {
//
//    private final JwtAuthenticationGatewayFilterFactory jwtAuthenticationGatewayFilterFactory;
//
//    public GatewayConfig(JwtAuthenticationGatewayFilterFactory jwtAuthenticationGatewayFilterFactory) {
//        this.jwtAuthenticationGatewayFilterFactory = jwtAuthenticationGatewayFilterFactory;
//    }
//
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                // Route for the Authentication Service
//                .route("auth_service_route", r -> r.path("/authenticate/**")
//                        .uri("http://localhost:8080"))
//
//                // Route for the Public Products Service endpoint
//                .route("products_public_route", r -> r.path("/products/public")
//                        .filters(f -> f.filter(jwtAuthenticationGatewayFilterFactory.apply(new JwtAuthenticationGatewayFilterFactory.Config() {{
//                            setIsPublic(true);
//                        }})))
//                        .uri("http://localhost:8083"))
//
//                // Route for the Secured Products Service endpoint
//                .route("products_secured_route", r -> r.path("/products/secured")
//                        .filters(f -> f.filter(jwtAuthenticationGatewayFilterFactory.apply(new JwtAuthenticationGatewayFilterFactory.Config() {{
//                            setIsPublic(false);
//                        }})))
//                        .uri("http://localhost:8083"))
//                .build();
//    }
//}


package com.example.api_gateway.config;

import com.example.api_gateway.filter.JwtAuthenticationGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationGatewayFilterFactory jwtAuthenticationGatewayFilterFactory;

    public GatewayConfig(JwtAuthenticationGatewayFilterFactory jwtAuthenticationGatewayFilterFactory) {
        this.jwtAuthenticationGatewayFilterFactory = jwtAuthenticationGatewayFilterFactory;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Route for the Authentication Service
                .route("auth_service_route", r -> r.path("/authenticate/**")
                        .uri("http://localhost:8080"))

                // Route for the Public Products Service endpoint
                .route("products_public_route", r -> r.path("/products/public")
                        .filters(f -> f.filter(jwtAuthenticationGatewayFilterFactory.apply(new JwtAuthenticationGatewayFilterFactory.Config() {{
                            setIsPublic(true);
                        }})))
                        .uri("http://localhost:8083"))

                // Route for a general secured endpoint (any valid token)
                .route("products_secured_route", r -> r.path("/products/secured")
                        .filters(f -> f.filter(jwtAuthenticationGatewayFilterFactory.apply(new JwtAuthenticationGatewayFilterFactory.Config() {{
                            setIsPublic(false);
                        }})))
                        .uri("http://localhost:8083"))

                // Route for a secured user-only endpoint
                .route("products_user_route", r -> r.path("/products/user")
                        .filters(f -> f.filter(jwtAuthenticationGatewayFilterFactory.apply(new JwtAuthenticationGatewayFilterFactory.Config() {{
                            setIsPublic(false);
                            setRequiredRole("ROLE_USER");
                        }})))
                        .uri("http://localhost:8083"))

                // Route for a secured admin-only endpoint
                .route("products_admin_route", r -> r.path("/products/admin", "/products/huhh")
                        .filters(f -> f.filter(jwtAuthenticationGatewayFilterFactory.apply(new JwtAuthenticationGatewayFilterFactory.Config() {{
                            setIsPublic(false);
                            setRequiredRole("ROLE_ADMIN");
                        }})))
                        .uri("http://localhost:8083"))
                .build();
    }
}
