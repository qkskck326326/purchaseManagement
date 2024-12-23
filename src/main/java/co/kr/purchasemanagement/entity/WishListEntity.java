package co.kr.purchasemanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wish_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishListEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "quantity")
    private int quantity;


    public WishListEntity(ProductEntity product, String userEmail, int quantity) {
        this.userEmail = userEmail;
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.quantity = quantity;
    }

}
