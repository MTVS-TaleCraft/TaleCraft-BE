package com.talecraft.talecraftbe.verification.repository;

import com.talecraft.talecraftbe.verification.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface EmailVerificationRepository
        extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByVerificationCode(String code);
    Optional<EmailVerification> findByEmail(String email);
    
    // 이메일로 모든 인증 레코드 조회
    List<EmailVerification> findAllByEmail(String email);
    
    // 이메일로 가장 최근 레코드 조회 (ID 기준)
    Optional<EmailVerification> findFirstByEmailOrderByIdDesc(String email);
}
