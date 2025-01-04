package co.kr.apigateway.security.filter;

import co.kr.apigateway.security.util.JwtTokenUtil;
import co.kr.apigateway.security.util.RedisUtil;
import co.kr.apigateway.security.util.GetUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.sql.SQLOutput;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final RedisUtil redisUtil;
    private final GetUserInfo getUserInfo;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        System.out.println("체인 시작");
        // Authorization 헤더에서 JWT 추출
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        System.out.println("authHeader = " + authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange); // JWT가 없으면 다음 필터로 진행
        }

        String token = authHeader.substring(7); // "Bearer " 제거
        String ip = getUserInfo.getClientIp(exchange.getRequest()); // 클라이언트 IP 추출
        String email = jwtTokenUtil.getUserEmailFromToken(token);
        System.out.println("ip = " + ip);

        System.out.println("JWT Token Validation: " + jwtTokenUtil.validateToken(token));
        System.out.println("Redis Login Validation: " + redisUtil.isValidLogin(email, ip));

        // JWT와 Redis 상태를 valid 메서드로 검증
        System.out.println("인증 결과 : " + (jwtTokenUtil.validateToken(token) && redisUtil.isValidLogin(email, ip)));
        if (jwtTokenUtil.validateToken(token) && redisUtil.isValidLogin(email, ip)) {
            return chain.filter(exchange); // 인증 통과, 다음 필터로 진행
        }

        // 인증 실패 시 401 응답
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        System.out.println("인증 실패!");
        return exchange.getResponse().setComplete();
    }
}
