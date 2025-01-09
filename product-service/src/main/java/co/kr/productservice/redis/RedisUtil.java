package co.kr.productservice.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;

    public void saveProductQuantity(long productId, int quantity) {
        redisTemplate.opsForValue().set(String.valueOf(productId), String.valueOf(quantity));
    }

    public Integer getProductQuantity(Long productId) {
        String quantity = redisTemplate.opsForValue().get(String.valueOf(productId));
        return quantity != null ? Integer.valueOf(quantity) : null;
    }

}
