package co.kr.orderservice.kafka.toProduct;

import co.kr.orderservice.order.entity.OrderItemRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
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

    public String decreaseQuantity(Long orderId, List<OrderItemRequestDto> orderItems) throws Exception {
        // 토픽 지정
        String topic = "product-quantity-decrease";
        // reply Id 생성
        String correlationId = UUID.randomUUID().toString();

        // 요청 메시지 구성
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("orderId", orderId);
        requestPayload.put("orderItems", orderItems);
        requestPayload.put("replyTopic", "product-quantity-decrease-reply");
        requestPayload.put("correlationId", correlationId);

        // 메시지를 JSON으로 변환
        String message = objectMapper.writeValueAsString(requestPayload);

        // 메시지 전송
        kafkaTemplate.send(topic, message);
        System.out.println("reply 용 correlationId: " + correlationId);

        // 응답 대기
        return ReplyService.getReply(correlationId);
    }

    public String increaseQuantity(Long orderId, List<OrderItemRequestDto> orderItems) throws Exception {
        // 토픽 지정
        String topic = "product-quantity-increase";
        String correlationId = UUID.randomUUID().toString();

        // 요청 메시지 구성
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("orderId", orderId);
        requestPayload.put("orderItems", orderItems);
        requestPayload.put("replyTopic", "product-quantity-increase-reply");
        requestPayload.put("correlationId", correlationId);

        // 메시지를 JSON으로 변환
        String message = objectMapper.writeValueAsString(requestPayload);

        // 메시지 전송
        kafkaTemplate.send(topic, message);
        System.out.println("reply 용 correlationId: " + correlationId);

        // 응답 대기
        return ReplyService.getReply(correlationId);
    }



}
