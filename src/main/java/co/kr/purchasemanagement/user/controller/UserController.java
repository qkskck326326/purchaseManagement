package co.kr.purchasemanagement.user.controller;

import co.kr.purchasemanagement.user.entity.UserEntity;
import co.kr.purchasemanagement.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 API
    @PostMapping("/register")
    public String sign(@RequestBody UserEntity user) {
        if (user.getUserName() == null || user.getEmail() == null || user.getPassword() == null) {
            return "이름, 전화번호, 주소는 반드시 입력하셔야 합니다.";
        }
        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.sign(user);
    }

    // 이메일 인증용 API
    @GetMapping("/verify")
    public void verifyUser(@RequestParam String code, @RequestParam String email) {
        userService.verifyEmail(code, email);
    }

    // 비밀번호 변경
    @PostMapping("/change-password")
    public String changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken, String password) {
        String hashedPassword = passwordEncoder.encode(password);
        return userService.changePassword(bearerToken, hashedPassword);
    }

}
