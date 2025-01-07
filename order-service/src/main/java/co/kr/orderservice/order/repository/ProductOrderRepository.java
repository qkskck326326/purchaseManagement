package co.kr.orderservice.order.repository;

import co.kr.orderservice.order.entity.ProductOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrderEntity, Long> {
    List<ProductOrderEntity> findAllByUserEmail(String userEmail);

    ProductOrderEntity findByOrderIdAndUserEmail(Long orderId, String userEmail);
}
