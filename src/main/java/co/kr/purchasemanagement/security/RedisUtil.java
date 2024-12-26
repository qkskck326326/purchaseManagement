package co.kr.purchasemanagement.security;

import lombok.AllArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean isValidLogin(String email, String ip) {
        // Redis 키와 필드 생성
        String redisKey = String.format("user:%s", email); // email을 키로 사용
        String fieldKey = String.format("ip:%s", DigestUtils.sha256Hex(ip)); // ip 필드 생성

        return redisTemplate.opsForHash().hasKey(redisKey, fieldKey); // Redis 에서 필드 확인, 존재하는지 리턴
    }

    public String getToken(String email, String ip) {
        // Redis 키와 필드 생성
        String redisKey = String.format("user:%s", email); // email을 키로 사용
        String fieldKey = String.format("ip:%s", DigestUtils.sha256Hex(ip)); // ip 필드 생성

        // Redis에서 필드 값(토큰) 조회
        Object token = redisTemplate.opsForHash().get(redisKey, fieldKey);

        // 토큰이 존재하면 반환, 없으면 null 반환
        return token != null ? token.toString() : null;
    }

    public void saveLoginInfo(String email, String ip, String token){
        redisTemplate.opsForValue().set(email, token, 60, TimeUnit.MINUTES); // 60분 TTL 설정

        String redisKey = String.format("user:%s", email); // email을 키로 사용
        String fieldKey = String.format("ip:%s", DigestUtils.sha256Hex(ip));

        // Hash에 User-Agent 필드 추가
        redisTemplate.opsForHash().put(redisKey, fieldKey, token);

        // TTL 설정 (선택적, email 키에 TTL 설정)
        redisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);
    }

    public void logoutHere(String email, String ip) {
        // Redis 키, 필드 생성
        String redisKey = String.format("user:%s", email);
        String fieldKey = String.format("ip:%s", DigestUtils.sha256Hex(ip));

        // email 과 agent 가 일치하는 정보 삭제
        redisTemplate.opsForHash().delete(redisKey, fieldKey);
    }

    public void logoutAll(String email) {
        // Redis 키 생성
        String redisKey = String.format("user:%s", email);

        // email 과 일치하는 모든 정보 삭제
        redisTemplate.delete(redisKey);
    }


}
