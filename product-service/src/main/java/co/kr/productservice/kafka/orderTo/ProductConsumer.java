package co.kr.productservice.kafka.orderTo;

import co.kr.productservice.product.entity.ProductEntity;
import co.kr.productservice.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class ProductConsumer {
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    // 재고감소요청 리스너
    @KafkaListener(topics = "product-quantity-decrease", groupId = "product-group")
    public void consumeDecreaseRequest(String message) throws Exception {
        // 요청 메시지 파싱
        Map<String, Object> request = objectMapper.readValue(message, Map.class);
        // OrderItemDto 리스트 변환
        List<Map<String, Object>> rawOrderItems = (List<Map<String, Object>>) request.get("orderItems");
        List<OrderItemDto> orderItems = rawOrderItems.stream()
                .map(item -> objectMapper.convertValue(item, OrderItemDto.class))
                .toList();

        String replyTopic = (String) request.get("replyTopic");
        String correlationId = (String) request.get("correlationId");

        // 재고 감소 처리
        String response = decreaseQuantity(orderItems);

        // 응답 메시지 구성
        Map<String, Object> responsePayload = new HashMap<>();
        responsePayload.put("correlationId", correlationId);
        responsePayload.put("response", response);

        // 응답 전송
        System.out.println("응답 topic : " + replyTopic);
        kafkaTemplate.send(replyTopic, objectMapper.writeValueAsString(responsePayload));
    }
    
    // 재고 감소 메소드
    private String decreaseQuantity(List<OrderItemDto> orderItems) {
        Map<Long, Integer> productList = new HashMap<>(); // 재고 감소시킨 상품 저장
        String result = ""; // 결과 저장
        int sum = 0;
        for (OrderItemDto orderItem : orderItems) { // 각 주문상품
            Optional<ProductEntity> productO = productRepository.findById(orderItem.getProductId());
            ProductEntity product;
            if (!productO.isPresent()) {
                result = "상품: " + orderItem.getProductName() + " 가 존재하지 않습니다.";
                break;
            }else {
                product = productO.get();
            }

            if(product.getBuyableTime() != null){
                // 상품 구매 가능 시간 확인
                System.out.println("product.getBuyableTime() - 구매가능 시간 = " + product.getBuyableTime());
                if (product.getBuyableTime().after(new Date())) {
                    result = "상품: " + orderItem.getProductName() + " 가 아직 구매 가능하지 않습니다.";
                    break;
                }
            }

            // 총 가격 계산
            sum += product.getPrice() * orderItem.getQuantity();
            // 재고 감소 및 감소되었는지 카운트
            int updated = productRepository.decreaseQuantity(orderItem.getProductId(), orderItem.getQuantity());
            if (updated == 0) {
                result = "상품: " + orderItem.getProductName() + "재고부족";
                break;
            }
            // 재고를 감소시킨 상품 저장
            productList.put(orderItem.getProductId(), orderItem.getQuantity());
        }
        // 재고 감소가 불가능한 ( 미존재 OR 재고부족 ) 상품이 있다면 재고 복구
        if (orderItems.size() > productList.size()){
            for (Map.Entry<Long, Integer> entry : productList.entrySet()) {
                productRepository.increaseQuantity(entry.getKey(), entry.getValue());
            }
        } else if (orderItems.size() == productList.size()) {
            result = "null" + sum;
        }

        return result;
    }
    
    // 재고증가 요청 리스너
    @KafkaListener(topics = "product-quantity-increase", groupId = "product-group")
    public void consumeIncreaseRequest(String message) throws Exception {
        // 요청 메시지 파싱
        Map<String, Object> request = objectMapper.readValue(message, Map.class);
        // OrderItemDto 리스트 변환
        List<Map<String, Object>> rawOrderItems = (List<Map<String, Object>>) request.get("orderItems");
        List<OrderItemDto> orderItems = rawOrderItems.stream()
                .map(item -> objectMapper.convertValue(item, OrderItemDto.class))
                .toList();

        String replyTopic = (String) request.get("replyTopic");
        String correlationId = (String) request.get("correlationId");

        // 재고 증가 처리
        String response = increaseQuantity(orderItems);

        // 응답 메시지 구성
        Map<String, Object> responsePayload = new HashMap<>();
        responsePayload.put("correlationId", correlationId);
        responsePayload.put("response", response);

        // 응답 전송
        kafkaTemplate.send(replyTopic, objectMapper.writeValueAsString(responsePayload));
    }
    
    // 재고 증가 메소드
    public String increaseQuantity(List<OrderItemDto> orderItems) {
        Map<Long, Integer> productList = new HashMap<>();
        String result = ""; // 결과 저장
        int updatedAll = 0;
        for (OrderItemDto orderItem : orderItems) {
            int updated = productRepository.increaseQuantity(orderItem.getProductId(), orderItem.getQuantity());
            updatedAll += updated;
            if (updated != 0) {
                productList.put(orderItem.getProductId(), orderItem.getQuantity());
            }
        }

        result = "null";

        return result;
    }

}