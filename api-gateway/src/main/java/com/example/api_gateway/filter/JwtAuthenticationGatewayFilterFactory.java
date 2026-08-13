package com.example.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilterFactory.Config> {

    @Value("${jwt.secret}")
    private String secret;

    public JwtAuthenticationGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (config.isPublic()) {
                return chain.filter(exchange);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return this.onError(exchange, "Authorization header is missing or invalid", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secret);
                SecretKey key = Keys.hmacShaKeyFor(keyBytes);
                Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

                List<String> roles = (List<String>) claims.get("roles");
                if (roles != null) {
                    if (config.getRequiredRole() != null && !config.getRequiredRole().isEmpty()) {
                        if (!roles.contains(config.getRequiredRole())) {
                            return this.onError(exchange, "Access denied: insufficient roles", HttpStatus.FORBIDDEN);
                        }
                    }

                    ServerHttpRequest modifiedRequest = request.mutate()
                            .header("X-User-Roles", String.join(",", roles))
                            .build();
                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                } else {
                    return this.onError(exchange, "JWT is missing user roles", HttpStatus.FORBIDDEN);
                }

            } catch (Exception e) {
                return this.onError(exchange, "JWT validation failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
        private boolean isPublic;
        private String requiredRole;

        public boolean isPublic() {
            return isPublic;
        }

        public void setIsPublic(boolean isPublic) {
            this.isPublic = isPublic;
        }

        public String getRequiredRole() {
            return requiredRole;
        }

        public void setRequiredRole(String requiredRole) {
            this.requiredRole = requiredRole;
        }
    }
}
