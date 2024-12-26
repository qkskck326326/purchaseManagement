package co.kr.purchasemanagement.security.filter;

import co.kr.purchasemanagement.security.GetUserInfo;
import co.kr.purchasemanagement.security.JwtAuthenticationToken;
import co.kr.purchasemanagement.security.JwtTokenUtil;
import co.kr.purchasemanagement.security.RedisUtil;
import co.kr.purchasemanagement.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisUtil redisUtil;
    private final GetUserInfo getUserInfo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if (token != null) {
            try {
                if (jwtTokenUtil.validateToken(token)) {
                    String email = jwtTokenUtil.getUserEmailFromToken(token);
                    String userIp = getUserInfo.getClientIp(request);

                    // Redis에서 email과 userIP 확인
                    if (!redisUtil.isValidLogin(email, userIp)) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                        response.getWriter().write("Invalid_login");
                        return;
                    }

                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                    if (userDetails != null) {
                        JwtAuthenticationToken authentication = new JwtAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                response.getWriter().write("Token_expired");
                return;
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                response.getWriter().write(e.getMessage());//////////////////////////////// !!
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
