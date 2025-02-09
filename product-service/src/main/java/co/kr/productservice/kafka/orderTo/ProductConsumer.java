package co.kr.productservice.kafka.orderTo;

import co.kr.productservice.product.entity.ProductEntity;
import co.kr.productservice.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class ProductConsumer {
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;

    // 트랜잭션 적용을 위한 자기 참조
    private ProductConsumer self;

    @PostConstruct
    private void init() {
        // Bean이 완전히 생성된 후 self를 안전하게 초기화
        this.self = applicationContext.getBean(ProductConsumer.class);
    }
    
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
        decreaseAllQuantity(orderItems);
    }
    
    // 전체 재고 감소 메소드
    private void decreaseAllQuantity(List<OrderItemDto> orderItems) {
        for (OrderItemDto orderItem : orderItems) {
            self.decreaseQuantity(orderItem.getProductId(), orderItem.getQuantity());
        }
    }

    // 재고 감소 메소드
    @Transactional
    public void decreaseQuantity(Long productId, Integer quantity) {
        // 비관적 락을 사용하여 DB에서 본 트랜잭션이 끝나기 전까지 변경을 대기하도록 함
        // ++ 이미 Kafka 메세지를 발송하기 전에 존재 확인이 끝났음으로 확인없이 처리
        ProductEntity product = productRepository.findProductToModifyQuantity(productId).orElse(null); // 비관적 락
        if (product != null) {
            product.setProductQuantity(product.getProductQuantity() - quantity); // 더디체킹
        }else {
            throw new RuntimeException("Product not found");
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
    
    // 전체 재고 증가 메소드
    public void increaseQuantity(List<OrderItemDto> orderItems) {
        for (OrderItemDto orderItem : orderItems) {
            self.increaseQuantity(orderItem.getProductId(), orderItem.getQuantity());
        }
    }

    // 재고 증가 메소드
    @Transactional
    public void increaseQuantity(Long productId, Integer quantity) {
        // 비관적 락을 사용하여 DB에서 본 트랜잭션이 끝나기 전까지 변경을 대기하도록 함
        // ++ 이미 Kafka 메세지를 발송하기 전에 존재 확인이 끝났음으로 확인없이 처리
        ProductEntity product = productRepository.findProductToModifyQuantity(productId).orElse(null); // 비관적 락
        if (product != null) {
            product.setProductQuantity(product.getProductQuantity() + quantity); // 더디체킹
        }else {
            throw new RuntimeException("Product not found");
        }
    }

}