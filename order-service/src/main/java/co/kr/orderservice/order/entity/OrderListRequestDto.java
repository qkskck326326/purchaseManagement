package co.kr.orderservice.order.entity;

import lombok.Data;

@Data
public class OrderListRequestDto{
    Long productId;
    String productName;
    int quantity;
}
