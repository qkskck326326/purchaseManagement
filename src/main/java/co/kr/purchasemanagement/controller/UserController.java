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
        return userService.sign(user);
    }

    @GetMapping("/verify")
    public void verifyUser(@RequestParam String code, @RequestParam String email) {
        userService.verifyEmail(code, email);
    }

}
