package co.kr.orderservice.kafka.toProduct;

import co.kr.orderservice.order.entity.OrderItemRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void decreaseQuantity(Long orderId, List<OrderItemRequestDto> orderItems) throws Exception {
        // 토픽 지정
        String topic = "product-quantity-decrease";

        // 요청 메시지 구성
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("orderId", orderId);
        requestPayload.put("orderItems", orderItems);

        // 메시지를 JSON으로 변환
        String message = objectMapper.writeValueAsString(requestPayload);

        // 메시지 전송
        kafkaTemplate.send(topic, message);
    }

    public void increaseQuantity(Long orderId, List<OrderItemRequestDto> orderItems) throws Exception {
        // 토픽 지정
        String topic = "product-quantity-increase";

        // 요청 메시지 구성
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("orderId", orderId);
        requestPayload.put("orderItems", orderItems);

        // 메시지를 JSON으로 변환
        String message = objectMapper.writeValueAsString(requestPayload);

        // 메시지 전송
        kafkaTemplate.send(topic, message);
    }



}
