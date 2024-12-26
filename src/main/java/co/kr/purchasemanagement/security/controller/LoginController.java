package co.kr.purchasemanagement.security.controller;

import co.kr.purchasemanagement.security.JwtTokenUtil;
import co.kr.purchasemanagement.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/security")
@AllArgsConstructor
public class LoginController {
    
    private LoginService loginService;
    
    // 로그인
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> loginData, HttpServletRequest request) {
        return loginService.login(loginData, request);
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String Authorization, HttpServletRequest request) {
        String token = null;
        if (Authorization.startsWith("Bearer ")) {
            token = Authorization.substring(7);
        }

        // 로그아웃
        loginService.logoutHere(token, request);

        return "로그아웃 완료";
    }

    @PostMapping("/logoutAll")
    public String logoutAll(@RequestHeader(HttpHeaders.AUTHORIZATION) String Authorization) {
        String token = null;
        if (Authorization.startsWith("Bearer ")) {
            token = Authorization.substring(7);
        }

        // 모든곳에서 로그아웃
        loginService.logoutAll(token);

        return "모든 장소에서 로그아웃 완료";
    }

}
