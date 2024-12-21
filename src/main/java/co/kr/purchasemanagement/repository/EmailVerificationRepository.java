package co.kr.purchasemanagement.repository;

import co.kr.purchasemanagement.entity.EmailVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerificationEntity, String> {

    boolean existsByEmailAndVerificationCode(String email, String code);
}
