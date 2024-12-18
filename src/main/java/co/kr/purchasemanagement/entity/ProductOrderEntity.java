package co.kr.purchasemanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import co.kr.purchasemanagement.entity.UserEntity;
import co.kr.purchasemanagement.entity.ProductEntity;

import java.util.Date;

@Entity
@Table(name = "product_order")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    private int quantity;

    @Column(name = "order_at")
    @Temporal(TemporalType.DATE)
    private Date orderAt;
}
