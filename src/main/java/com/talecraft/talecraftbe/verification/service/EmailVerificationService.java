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

    /** 토큰 생성 후 메일 발송 */
    public void createAndSendToken(String email) {
        String code = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(1, ChronoUnit.DAYS);

        EmailVerification ev = new EmailVerification(email, code, expiry);
        repo.save(ev);

        String link = "http://.../api/verification?code=" + code;
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("이메일 인증");
        mail.setText("인증 링크:\n" + link);
        mailSender.send(mail);
    }

    /** 토큰 검증 */
    public boolean verify(String code) {
        return repo.findByVerificationCode(code)
                .filter(e -> !e.isVerified() && e.getExpiredDate().isAfter(Instant.now()))
                .map(e -> {
                    e.setVerified(true);
                    e.setSignedUp(true);
                    repo.save(e);
                    return true;
                }).orElse(false);
    }
}

