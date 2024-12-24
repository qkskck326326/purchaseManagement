package co.kr.purchasemanagement.user.repository;

import co.kr.purchasemanagement.user.entity.EmailVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerificationEntity, String> {

    boolean existsByEmailAndVerificationCode(String email, String code);
}
