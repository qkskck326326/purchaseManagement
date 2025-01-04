package co.kr.apigateway.security.util;

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

}
