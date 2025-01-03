package co.kr.purchasemanagement.order.repository;

import co.kr.purchasemanagement.order.entity.WishListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishListEntity, Long> {
    boolean existsByProductIdAndUserEmail(Long productId, String userEmail);

    boolean existsByUserEmail(String userEmail);

    List<WishListEntity> findByUserEmail(String userEmail);
}
