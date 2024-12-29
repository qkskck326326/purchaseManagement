package co.kr.orderservice.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "order_state")
    private OrderStateEnum orderState = OrderStateEnum.Order_Completed;

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
        this.orderAt = new Date();
    }

    public ProductOrderEntity(String userEmail) {
        this.userEmail = userEmail;
    }
}
