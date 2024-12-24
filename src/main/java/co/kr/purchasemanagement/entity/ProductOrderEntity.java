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
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    private UserEntity userEmail;

    @Column(name = "order_state")
    private OrderStateEnum orderState;

    @Column(name = "order_at", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date orderAt;

    @Column(name = "update_at")
    @Temporal(TemporalType.DATE)
    private Date updateAt;

    @Column(name = "refund_at")
    @Temporal(TemporalType.DATE)
    private Date refundAt;

    @PrePersist
    protected void onCreate() {
        orderAt = new Date();
        this.orderState = OrderStateEnum.Order_Completed;
    }

    public ProductOrderEntity(String userEmail) {
        this.userEmail = new UserEntity();
        this.userEmail.setEmail(userEmail);
    }
}
