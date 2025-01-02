package co.kr.productservice.product.repository;

import co.kr.productservice.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE ProductEntity p SET p.productQuantity = p.productQuantity - :amount WHERE p.productId = :productId AND p.productQuantity >= :amount")
    int decreaseQuantity(@Param("productId") Long productId, @Param("amount") int amount);

    @Modifying
    @Transactional
    @Query("UPDATE ProductEntity p SET p.productQuantity = p.productQuantity + :amount WHERE p.productId = :productId AND p.productQuantity >= :amount")
    int increaseQuantity(@Param("productId") Long productId, @Param("amount") int amount);
}
