package com.example.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * This bean configures the Security Web Filter Chain for the API Gateway.
     * We need to disable default Spring Security behaviors like form login and CSRF
     * so that our custom JWT authentication filter can handle all security logic.
     * The `permitAll()` ensures all requests pass through the Spring Security filter,
     * delegating the actual authorization to our GatewayFilters.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // Disable CSRF since we are using a token-based authentication model.
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // Disable form login, as the gateway should not have a login page.
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                // Authorize all requests. The actual security logic is in our custom GatewayFilter.
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                .build();
    }
}
