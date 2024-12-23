package co.kr.purchasemanagement.entity;

import co.kr.purchasemanagement.dto.OrderListRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_order_list")
@Getter @Setter
@NoArgsConstructor
public class ProductOrderListEntity {
    @Id
    @Column(name = "order_id")
    Long orderId;

    @Column(name = "productId")
    Long productId;

    @Column(name = "productName")
    String productName;

    @Column(name = "quantity")
    int quantity;

    public ProductOrderListEntity(Long orderId, OrderListRequestDto orderListRequestDto) {
        this.orderId = orderId;
        this.productId = orderListRequestDto.getProductId();
        this.productName = orderListRequestDto.getProductName();
        this.quantity = orderListRequestDto.getQuantity();
    }

}
