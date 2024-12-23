package co.kr.purchasemanagement.repository;

import co.kr.purchasemanagement.entity.ProductOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrderEntity, Long> {
}
