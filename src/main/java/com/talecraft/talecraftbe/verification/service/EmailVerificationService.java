package com.talecraft.talecraftbe.verification.service;

import com.talecraft.talecraftbe.verification.entity.EmailVerification;
import com.talecraft.talecraftbe.verification.repository.EmailVerificationRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class EmailVerificationService {

    private final EmailVerificationRepository repo;
    private final JavaMailSender mailSender;

    public EmailVerificationService(
            EmailVerificationRepository repo,
            JavaMailSender mailSender) {
        this.repo = repo;
        this.mailSender = mailSender;
    }

    /** 이메일 인증 토큰 생성 후 메일 발송 */
    public void createAndSendToken(String email) {
        String code = UUID.randomUUID().toString();
        Instant expiredDate = Instant.now().plus(1, ChronoUnit.DAYS);
        
        // 인증용 정보만 DB에 저장
        EmailVerification ev = new EmailVerification(email, code, expiredDate);
        repo.save(ev);
        
        String link = "http://localhost:8081/api/verification?code=" + code + "&email=" + email;
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("이메일 인증");
        mail.setText("인증 링크:\n" + link + "\n\n링크를 클릭하여 이메일 인증을 완료해주세요.");
        mailSender.send(mail);
    }

    /** 토큰 검증 (VERIFIED = 1로 변경) */
    public boolean verify(String code, String email) {
        return repo.findByVerificationCode(code)
                .filter(e -> e.getEmail().equals(email))
                .filter(e -> !e.isVerified())
                .filter(e -> e.getExpiredDate().isAfter(Instant.now()))
                .map(e -> {
                    // 인증 완료 처리 (VERIFIED = 1)
                    e.setVerified(true);
                    repo.save(e);
                    return true;
                }).orElse(false);
    }

    /** 이메일로 인증 완료 여부 확인 */
    public boolean isEmailVerified(String email) {
        return repo.findByEmail(email)
                .map(EmailVerification::isVerified)
                .orElse(false);
    }

    /** 회원가입 완료 처리 (SIGNED_UP = 1로 변경) */
    public void markAsSignedUp(String email) {
        repo.findByEmail(email)
                .filter(EmailVerification::isVerified)
                .ifPresent(e -> {
                    e.setSignedUp(true);
                    repo.save(e);
                });
    }

    /** 이메일로 회원가입 완료 여부 확인 */
    public boolean isSignedUp(String email) {
        return repo.findByEmail(email)
                .map(EmailVerification::isSignedUp)
                .orElse(false);
    }
}

