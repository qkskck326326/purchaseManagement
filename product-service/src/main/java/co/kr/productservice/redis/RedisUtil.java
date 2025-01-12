package co.kr.productservice.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@AllArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;

    public void saveProduct(long productId, int quantity, int price) {
        System.out.println("productId = " + productId);
        System.out.println("quantity = " + quantity);
        System.out.println("price = " + price);
        String script = """
            local productId = KEYS[1]
            local quantity = ARGV[1]
            local price = ARGV[2]
            redis.call("HSET", productId, "quantity", quantity, "price", price)
            return "OK"
            """;

        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(String.class);

        redisTemplate.execute(
                redisScript,
                Collections.singletonList(String.valueOf(productId)), // KEYS
                String.valueOf(quantity), String.valueOf(price) // ARGV
        );
    }

    public Integer getProductQuantity(long productId) {
        String script = """
        local productId = KEYS[1]
        local quantity = redis.call("HGET", productId, "quantity")
        if quantity == nil then
            return -1
        end
        return tonumber(quantity)
        """;

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);

        Long result = redisTemplate.execute(
                redisScript,
                Collections.singletonList(String.valueOf(productId)) // KEYS
        );

        System.out.println("result = " + result);

        if (result == null || result == -1) {
            System.out.println("if문 실행됨");
            return null; // Redis에서 값이 없을 때 null 반환
        }

        System.out.println("if문 실행 안됨");
        return Integer.valueOf(String.valueOf(result));
    }

    public Integer getProductPrice(long productId) {
        String script = """
        local productId = KEYS[1]
        local price = redis.call("HGET", productId, "price")
        if price == nil then
            return -1
        end
        return price
        """;

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(String.class);

        String result = redisTemplate.execute(
                redisScript,
                Collections.singletonList(String.valueOf(productId)) // KEYS
        );

        System.out.println("result = " + result);

        if (result == null || result.equals("-1")) {
            System.out.println("if문 실행됨");
            return null; // Redis에서 값이 없을 때 null 반환
        }

        System.out.println("if문 실행 안됨");
        return Integer.valueOf(result);
    }

}
