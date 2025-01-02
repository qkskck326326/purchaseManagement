package co.kr.orderservice.order.entity;

import lombok.Data;

@Data
public class OrderItemRequestDto {
    Long productId;
    String productName;
    int quantity;

    public OrderItemRequestDto(ProductOrderItemEntity productOrderItemEntity) {
        this.productId = productOrderItemEntity.getProductId();
        this.productName = productOrderItemEntity.getProductName();
        this.quantity = productOrderItemEntity.getQuantity();
    }
}
