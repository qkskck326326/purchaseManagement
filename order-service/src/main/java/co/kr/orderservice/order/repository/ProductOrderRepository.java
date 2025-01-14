package co.kr.orderservice.order.repository;

import co.kr.orderservice.order.entity.ProductOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrderEntity, Long> {
    List<ProductOrderEntity> findAllByUserEmail(String userEmail);

    Optional<ProductOrderEntity> findByOrderIdAndUserEmail(Long orderId, String userEmail);
}
