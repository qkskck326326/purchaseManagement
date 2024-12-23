package co.kr.purchasemanagement.controller;

import co.kr.purchasemanagement.entity.UserEntity;
import co.kr.purchasemanagement.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/sign")
    public String sign(@RequestBody UserEntity user) {
        if (user.getUserName() == null || user.getEmail() == null || user.getPassword() == null) {
            return "이름, 전화번호, 주소는 반드시 입력하셔야 합니다.";
        }
        return userService.sign(user);
    }

    @GetMapping("/verify")
    public void verifyUser(@RequestParam String code, @RequestParam String email) {
        userService.verifyEmail(code, email);
    }

}
