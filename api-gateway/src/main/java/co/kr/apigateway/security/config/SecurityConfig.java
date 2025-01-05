package co.kr.apigateway.security.config;

import co.kr.apigateway.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/user/**").permitAll() // User-Service 경로 허용
                        .pathMatchers("/api/order/**", "/api/product/**").permitAll() // Order, Product 허용
                        .anyExchange().authenticated() // 나머지 요청은 인증 필요
                )
                .addFilterBefore((exchange, chain) -> {
                    String path = exchange.getRequest().getURI().getPath();
                    // 이부분에 필터를 적용하지 않을 api 요청 추가
                    if (path.startsWith("/api/user/") || path.startsWith("/api/order/") || path.startsWith("/api/product/")) {
                        return chain.filter(exchange); // 필터 제외
                    }
                    return jwtAuthenticationFilter.filter(exchange, chain); // 필터 실행
                }, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}
