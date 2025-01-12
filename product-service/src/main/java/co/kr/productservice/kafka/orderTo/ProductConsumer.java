package co.kr.productservice.kafka.orderTo;

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
        // 메세지를 OrderItemDto 리스트 변환
        List<Map<String, Object>> rawOrderItems = (List<Map<String, Object>>) request.get("orderItems");
        List<OrderItemDto> orderItems = rawOrderItems.stream()
                .map(item -> objectMapper.convertValue(item, OrderItemDto.class))
                .toList();

        // 재고 감소 처리
        decreaseQuantity(orderItems);
    }
    
    // 재고 감소 메소드
    private void decreaseQuantity(List<OrderItemDto> orderItems) {
        for (OrderItemDto orderItem : orderItems) {
            productRepository.decreaseQuantity(orderItem.getProductId(), orderItem.getQuantity());
        }
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

        // 재고 증가 처리
        increaseQuantity(orderItems);
    }
    
    // 재고 증가 메소드
    public void increaseQuantity(List<OrderItemDto> orderItems) {
        for (OrderItemDto orderItem : orderItems) {
            productRepository.increaseQuantity(orderItem.getProductId(), orderItem.getQuantity());
        }
    }

}