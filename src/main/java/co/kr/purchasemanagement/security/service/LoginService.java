package co.kr.purchasemanagement.security.service;

import co.kr.purchasemanagement.security.GetUserInfo;
import co.kr.purchasemanagement.security.JwtTokenUtil;
import co.kr.purchasemanagement.security.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class LoginService {
    private AuthenticationManager authenticationManager; // 인증 객체
    private JwtTokenUtil jwtTokenUtil; // 토큰 생성 및 인증 객체
    private RedisUtil redisUtil;
    private GetUserInfo getUserInfo;

    public String login(Map<String, String> loginData, HttpServletRequest request) {
        String email = loginData.get("email");
        String password = loginData.get("password");
        // user-ip 추출
        String userIp = getUserInfo.getClientIp(request);

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

            // 인증 성공 시 JWT 생성
            String token = "Bearer " + jwtTokenUtil.createToken(authentication);

            // 유저 로그인 처리 ( redis )
            redisUtil.saveLoginInfo(email, userIp, token);

            // 응답으로 JWT 반환
            return token;

        } catch (AuthenticationException e) {
            return "이메일과 비밀번호가 일치하지 않습니다." + e.getMessage();
        }
    }

    public String logoutHere(String token, HttpServletRequest request) {
        // User-ip 추출
        String userIp = getUserInfo.getClientIp(request);
        // 토큰에서 이메일 정보 가져오기
        String email = jwtTokenUtil.getUserEmailFromToken(token);

        // 요청 장소의 로그인 정보 삭제
        redisUtil.logoutHere(email, userIp);

        return "로그아웃 되었습니다.";
    }

    public String logoutAll(String token) {
        String email = jwtTokenUtil.getUserEmailFromToken(token);
        // 모든 장소에서 해당 이메일로의 로그인 정보 삭제
        redisUtil.logoutAll(email);

        return "모든 기기에서 로그아웃 되었습니다.";
    }

}
