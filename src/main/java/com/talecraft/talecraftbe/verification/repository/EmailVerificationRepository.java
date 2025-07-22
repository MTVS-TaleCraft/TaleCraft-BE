package com.talecraft.talecraftbe.verification.repository;

import com.talecraft.talecraftbe.verification.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailVerificationRepository
        extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByVerificationCode(String code);
    Optional<EmailVerification> findByEmail(String email);
}
