package co.kr.orderservice.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_order_list")
@Getter @Setter
@NoArgsConstructor
public class ProductOrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long itemId;

    @Column(name = "order_id")
    Long orderId;

    @Column(name = "productId")
    Long productId;

    @Column(name = "productName")
    String productName;

    @Column(name = "quantity")
    int quantity;

    public ProductOrderItemEntity(Long orderId, OrderItemRequestDto orderListRequestDto) {
        this.orderId = orderId;
        this.productId = orderListRequestDto.getProductId();
        this.productName = orderListRequestDto.getProductName();
        this.quantity = orderListRequestDto.getQuantity();
    }

}
