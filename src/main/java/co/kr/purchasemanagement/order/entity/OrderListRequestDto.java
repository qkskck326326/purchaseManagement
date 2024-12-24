package co.kr.purchasemanagement.order.entity;

import lombok.Data;

@Data
public class OrderListRequestDto{
    Long productId;
    String productName;
    int quantity;
}
