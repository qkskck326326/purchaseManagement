package co.kr.userservice.user.service;

import co.kr.userservice.security.JwtTokenUtil;
import co.kr.userservice.security.RedisUtil;
import co.kr.userservice.user.entity.EmailVerificationEntity;
import co.kr.userservice.user.entity.UserEntity;
import co.kr.userservice.user.repository.EmailVerificationRepository;
import co.kr.userservice.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final JwtTokenUtil jwtTokenUtil;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final RedisUtil redisUtil;

    public String sign(UserEntity user) {
        // 아이디가 이미 존재하면
        if(userRepository.findById(user.getEmail()).isPresent()){
            return "이미 존재하는 email 입니다.";
        }else {
            userRepository.save(user);
            sendEmail(user.getEmail());
            return "회원가입 완료, 이메일 인증 이후 로그인 해 주세요";
        }
    }

    public void verifyEmail(String code, String email) {
        if(emailVerificationRepository.existsByEmailAndVerificationCode(email, code)){
            System.out.println("인증완료");
            UserEntity user = userRepository.findByEmail(email);
            user.setVerify(true);
            userRepository.save(user);
        }
    }

    public void sendEmail(String to) {
        //////////////////////////////////////////////////////////////////////////// 편집점
        String hostName = "localhost";
        int portNumber = 8080;
        
        System.out.println("hostname = " + hostName);
        System.out.println("portNumber = " + portNumber);
        // 코드 생성
        String verificationCode = UUID.randomUUID().toString();

        String subject = "회원가입 이메일 인증";
        String content = "다음 링크를 클릭하여 이메일 인증을 완료하세요: "
                + "http://" + hostName + ":"+ portNumber + "/api/user/common/verify?code=" + verificationCode + "&email=" + to;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to); // 수신자 이메일
        message.setSubject(subject); // 이메일 제목
        message.setText(content); // 이메일 본문
        message.setFrom("purchaseSparta@gmail.com"); // 발신자 이메일

        // 메일 보내기
        mailSender.send(message);
        // 이메일 및 코드 저장
        emailVerificationRepository.save(new EmailVerificationEntity(to, verificationCode));

    }

    @Transactional
    public String changePassword(String bearerToken, String password) {
        // 토큰 이메일로 변환
        String token = bearerToken.substring(7);
        String email = jwtTokenUtil.getUserEmailFromToken(token);
        
        try {
            UserEntity user = userRepository.findByEmail(email); // 이메일로 해당 유저 가져오기
            user.setPassword(password); // 비밀번호 변경
            userRepository.save(user); // 저장
    
            redisUtil.logoutAll(email); // 변경 완료시 모든곳에서 로그아웃
        } catch (Exception e) {
            return "비밀번호 변경 에러 : " + e.getMessage();
        }
        return "비밀번호 변경 완료, 모든 장소에서 로그아웃 처리되었습니다.";
    }
}
