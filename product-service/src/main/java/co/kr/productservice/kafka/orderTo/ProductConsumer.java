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

        // 로직 처리 (예: 재고 감소)
        String response = decreaseQuantity(orderItems);

        // 응답 메시지 구성
        Map<String, Object> responsePayload = new HashMap<>();
        responsePayload.put("correlationId", correlationId);
        responsePayload.put("response", response);

        // 응답 전송
        System.out.println("응답메세지 전송");
        kafkaTemplate.send(replyTopic, objectMapper.writeValueAsString(responsePayload));
        System.out.println("응답메세지 전송 완료");
    }
    
    // 재고 감소 메소드
    private String decreaseQuantity(List<OrderItemDto> orderItems) {
        Map<Long, Integer> productList = new HashMap<>(); // 재고 감소시킨 상품 저장
        String result = ""; // 결과 저장
        for (OrderItemDto orderItem : orderItems) { // 각 주문상품
            if (!productRepository.existsById(orderItem.getProductId())) {
                result = "상품: " + orderItem.getProductName() + " 가 존재하지 않습니다.";
                break;
            }
            int updated = productRepository.decreaseQuantity(orderItem.getProductId(), orderItem.getQuantity());
            if (updated == 0) {
                result = "상품: " + orderItem.getProductName() + "재고부족";
                break;
            }
            productList.put(orderItem.getProductId(), orderItem.getQuantity());
        }
        if (orderItems.size() > productList.size()){
            for (Map.Entry<Long, Integer> entry : productList.entrySet()) {
                productRepository.increaseQuantity(entry.getKey(), entry.getValue());
            }
        } else if (orderItems.size() == productList.size()) {
            result = "null";
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

        // 로직 처리 (예: 재고 감소)
        String response = increaseQuantity(orderItems);

        // 응답 메시지 구성
        Map<String, Object> responsePayload = new HashMap<>();
        responsePayload.put("correlationId", correlationId);
        responsePayload.put("response", response);

        // 응답 전송
        System.out.println("응답메세지 전송");
        kafkaTemplate.send(replyTopic, objectMapper.writeValueAsString(responsePayload));
        System.out.println("응답메세지 전송 완료");
    }
    
    // 재고 증가 메소드
    public String increaseQuantity(List<OrderItemDto> orderItems) {
        Map<Long, Integer> productList = new HashMap<>();
        String result = ""; // 결과 저장
        for (OrderItemDto orderItem : orderItems) {
            if (!productRepository.existsById(orderItem.getProductId())) {
                int updated = productRepository.increaseQuantity(orderItem.getProductId(), orderItem.getQuantity());
                if (updated != 0) {
                    productList.put(orderItem.getProductId(), orderItem.getQuantity());
                }
            }
        }

        result = "null";

        return result;
    }

}