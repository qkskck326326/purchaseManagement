package co.kr.purchasemanagement.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "user_email", nullable = false)
    private UserEntity user;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "order_at", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date orderAt;

    @PrePersist
    protected void onCreate() {
        orderAt = new Date();
    }
}
