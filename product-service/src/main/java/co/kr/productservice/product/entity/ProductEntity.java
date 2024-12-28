package co.kr.productservice.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "product")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_quantity")
    private int productQuantity;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "create_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    @Column(name = "update_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateAt;

    @PrePersist
    protected void onCreate() {
        createAt = new Date();
        updateAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = new Date();
    }

}
