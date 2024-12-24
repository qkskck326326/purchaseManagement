package co.kr.purchasemanagement.order.repository;

import co.kr.purchasemanagement.order.entity.ProductOrderListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderListRepository extends JpaRepository<ProductOrderListEntity, Long> {
    List<ProductOrderListEntity> findAllByOrderId(Long orderId);
}
