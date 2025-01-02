package co.kr.productservice.kafka.orderTo;

import lombok.Data;

@Data
public class OrderItemDto {
    Long productId;
    String productName;
    int quantity;
}
