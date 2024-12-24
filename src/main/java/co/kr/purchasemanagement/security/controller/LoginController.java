package co.kr.purchasemanagement.security.controller;

import co.kr.purchasemanagement.security.JwtTokenUtil;
import co.kr.purchasemanagement.service.CustomUserDetailsService;
import co.kr.purchasemanagement.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/security")
@AllArgsConstructor
public class LoginController {
    
    private AuthenticationManager authenticationManager; // 인증 객체
    private JwtTokenUtil jwtTokenUtil; // 토큰 생성 및 인증 객체
    private CustomUserDetailsService customUserDetailsService; // 인증을 위한 UserDetail 생성 객체
    private UserService userService; // 유저 정보를 처리하기 위한 객체

    // private final GetUserInfo getUserInfo; // 유저가 로그인한곳을 알아내기 위한 객체 - 구현 필요
    
    // 로그인
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> loginData){
        String email = loginData.get("email");
        String password = loginData.get("password");

        try {
            // 이메일과 비밀번호 유효성 검사
            if (email == null || email.isEmpty()) {
                return "이메일을 입력해 주세요";
            }
            if (password == null || password.isEmpty()) {
                return "비밀번호를 입력해 주세요";
            }

            // 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            // 유저 로그인 처리 ( redis )

            // 인증 성공 시 JWT 생성 &&
            // 응답으로 JWT 반환
            return "Bearer " + jwtTokenUtil.createToken(authentication);

        } catch (AuthenticationException e) {
            return "이메일과 비밀번호가 일치하지 않습니다.";
        }
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String Authorization) {
        String token;
        if (Authorization.startsWith("Bearer ")) {
            token = Authorization.substring(7);
        }
        
        // 로그아웃 로직 여기
        
        return "로그아웃 완료";
    }

    @PostMapping("/logoutAll")
    public String logoutAll(@RequestHeader(HttpHeaders.AUTHORIZATION) String Authorization) {
        String token;
        if (Authorization.startsWith("Bearer ")) {
            token = Authorization.substring(7);
        }

        // 로그아웃 로직 여기

        return "모든 장소에서 로그아웃 완료";
    }

}
