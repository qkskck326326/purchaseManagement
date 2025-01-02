package co.kr.orderservice.kafka.toProduct;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class ReplyService {
    private static final Map<String, BlockingQueue<String>> replyQueues = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "product-quantity-decrease-reply", groupId = "product-group")
    public void consumeReply(String message) throws Exception {
        // 응답 메시지 파싱
        Map<String, Object> response = objectMapper.readValue(message, Map.class);
        String correlationId = (String) response.get("correlationId");
        String reply = (String) response.get("response");

        // 응답을 대기 중인 큐에 전달
        replyQueues.computeIfPresent(correlationId, (key, queue) -> {
            queue.offer(reply);
            return queue;
        });
    }

    public static String getReply(String correlationId) throws InterruptedException, TimeoutException {
        BlockingQueue<String> queue = replyQueues.computeIfAbsent(correlationId, key -> new ArrayBlockingQueue<>(1));
        String reply = queue.poll(10, TimeUnit.SECONDS); // 응답 대기 시간 10초
        if (reply == null) {
            throw new TimeoutException("No reply received within the timeout period");
        }
        return reply;
    }
}
