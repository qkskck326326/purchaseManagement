package co.kr.purchasemanagement.repository;

import co.kr.purchasemanagement.entity.WishListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishListRepository extends JpaRepository<WishListEntity, Long> {
    boolean existsByProductIdAndUserEmail(Long productId, String userEmail);

    boolean existsByUserEmail(String userEmail);

    List<WishListEntity> findByUserEmail(String userEmail);
}
