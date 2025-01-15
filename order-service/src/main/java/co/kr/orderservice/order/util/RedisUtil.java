package co.kr.orderservice.order.util;

import co.kr.orderservice.order.entity.OrderItemRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@AllArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;

    // 여기서 null이 나온다. -- 수정점
    static String decreaseQuantity = """
        local productId = KEYS[1]
        local quantity0 = tonumber(ARGV[1])
        local quantity1 = tonumber(redis.call("HGET", productId, "quantity"))
        local price = redis.call("HGET", productId, "price")
        
        if not quantity1 then
            return nil
        end
        
        if quantity1 >= quantity0 then
            local newQuantity = quantity1 - quantity0
            redis.call("HSET", productId, "quantity", newQuantity)
            return price
        else
            return "less"
        end
        """;
    static String increaseQuantity = """
        local productId = KEYS[1]
        local quantity0 = tonumber(ARGV[1])
        local quantity1 = tonumber(redis.call("HGET", productId, "quantity"))
        
        
        local newQuantity = quantity1 + quantity0
        redis.call("HSET", productId, "quantity", newQuantity)
        return productId
        """;

    static String existProduct = """
        local productId = KEYS[1]
        if redis.call("HEXISTS", productId, "quantity") == 0 then
            return nil
        else
            return "exist"
        end
        """;
    
    // 재고 감소 메소드
    public Object[] decreaseProductQuantityList(List<OrderItemRequestDto> items) {
        String itemState = "";
        int sum = 0;

        // 재고 감소 스크립드 적용
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(decreaseQuantity);
        redisScript.setResultType(String.class);
        
        // 처리 성공한 Item 저장
        List<OrderItemRequestDto> processedItem = new ArrayList<>();
        
        // 재고 확인 및 감소 처리
        System.out.println("재고감소처리 : 시작");
        for (OrderItemRequestDto item : items) {
            // 재고 감소에 성공하면 해당 상품의 가격이 리턴됨
            String result = decreaseProductQuantity(redisScript, item);
            System.out.println("result = " + result);
            if (result == null) { // price 가 null == 상품이 없다
                itemState = item.getProductName() + " 이 존재하지 않습니다.";
                break;
            } else if (result.equals("less")) { // price 가 -1 == 재고가 부족하다.
                itemState = item.getProductName() + " 의 재고가 부족합니다.";
                break;
            }else {
                int price = Integer.parseInt(result); // 가격 down 캐스팅
                processedItem.add(item); // 처리된 Item 리스트에 저장
                sum += item.getQuantity() * price; // 총합 += 상품갯수 * 가격
            }
        }
        System.out.println("재고감소처리 : 끝");

        // 처리된 Item 의 숫자가 처리해야했을 갯수보다 작다
        // == 처리 중간에 생긴 문제로 다 처리되지 못함
        // == 주문 처리 실패
        if (processedItem.size() < items.size()) {
            System.out.println("주문 복구 처리 : 시작");
            redisScript.setScriptText(increaseQuantity); // 감소 스크립트로 변경
            for (OrderItemRequestDto item : processedItem) { // 처리되었던 Item 리스트 복구
                increaseProductQuantity(redisScript, item);
            }
            System.out.println("주문 복구 처리 : 종료");
        }else { // 성공시
            itemState = null;
        }

        return new Object[]{itemState, sum};
    }

    public String increaseProductQuantityList(List<OrderItemRequestDto> items){
        String result = "";

        try {
            // 재고 증가 스크립드 적용
            DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(increaseQuantity);
            redisScript.setResultType(String.class);

            for (OrderItemRequestDto item : items) {
                String redisResult = increaseProductQuantity(redisScript, item);
                System.out.println("redisResult = " + redisResult);
                if (redisResult == null) {
                    // 수정점 - 이부분에 삭제되었으나 재고 증가 처리되는 로직 추가 가능
                }
            }
            result = "success";
        } catch (Exception e) {
            System.out.println("상품 재고 증가 중 에러 : " + e.getMessage());
            result = e.getMessage();
        }

        return result;
    }

    public String decreaseProductQuantity(DefaultRedisScript<String> redisScript, OrderItemRequestDto item) {
        try {
            return redisTemplate.execute(
                    redisScript,
                    Collections.singletonList(String.valueOf(item.getProductId())),
                    String.valueOf(item.getQuantity())
            );
        } catch (Exception e) {
            System.err.println("Redis 호출 실패 - Product ID: " + item.getProductId() +
                    ", Quantity: " + item.getQuantity() +
                    ", Error: " + e.getMessage());
            return null;
        }
    }

    public String increaseProductQuantity(DefaultRedisScript<String> redisScript, OrderItemRequestDto item) {
        return redisTemplate.execute(
                redisScript,
                Collections.singletonList(String.valueOf(item.getProductId())),
                String.valueOf(item.getQuantity())
        );
    }

    
    // 해당 상품이 존재하는지 확인
    public boolean existProduct(Long productId){
        boolean result = false;
        // 존재확인 스크립트 세팅
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(existProduct);
        redisScript.setResultType(String.class);

        String resultRedis = redisTemplate.execute(
                redisScript,
                Collections.singletonList(String.valueOf(productId))
        );

        if ("exist".equals(resultRedis)) {
            result = true;
        }

        return result;
    }


}
