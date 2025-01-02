package co.kr.orderservice.order.repository;


import co.kr.orderservice.order.entity.ProductOrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderListRepository extends JpaRepository<ProductOrderItemEntity, Long> {
    List<ProductOrderItemEntity> findAllByOrderId(Long orderId);
}
