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

    /** 토큰 생성 후 메일 발송 (회원가입 정보 포함) */
    public void createAndSendToken(String email, String userName, String password) {
        String code = UUID.randomUUID().toString();
        Instant expiredDate = Instant.now().plus(1, ChronoUnit.DAYS);
        
        // 회원가입 정보와 함께 DB에 저장
        EmailVerification ev = new EmailVerification(email, code, expiredDate, userName, password);
        repo.save(ev);
        
        String link = "http://localhost:8080/api/verification?code=" + code + "&email=" + email;
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("이메일 인증");
        mail.setText("인증 링크:\n" + link + "\n\n링크를 클릭하여 이메일 인증을 완료해주세요.");
        mailSender.send(mail);
    }

    /** 토큰 검증 */
    public boolean verify(String code, String email) {
        return repo.findByVerificationCode(code)
                .filter(e -> e.getEmail().equals(email))
                .filter(e -> !e.isVerified())
                .filter(e -> e.getExpiredDate().isAfter(Instant.now()))
                .map(e -> {
                    // 인증 완료 처리
                    e.setVerified(true);
                    e.setSignedUp(true);
                    repo.save(e);
                    return true;
                }).orElse(false);
    }

    /** 인증된 회원가입 정보 조회 */
    public EmailVerification getVerifiedSignupInfo(String code, String email) {
        return repo.findByVerificationCode(code)
                .filter(e -> e.getEmail().equals(email))
                .filter(e -> e.isVerified())
                .filter(e -> e.isSignedUp())
                .orElse(null);
    }
}

