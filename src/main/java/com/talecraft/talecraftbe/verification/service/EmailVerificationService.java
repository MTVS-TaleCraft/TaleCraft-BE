package com.talecraft.talecraftbe.verification.service;

import com.talecraft.talecraftbe.verification.entity.EmailVerification;
import com.talecraft.talecraftbe.verification.repository.EmailVerificationRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

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

    /** 4자리 인증 코드 생성 */
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    /** 이메일 인증 토큰 생성 후 메일 발송 */
    public String createAndSendToken(String email) {
        String code = generateVerificationCode();
        Instant expiredDate = Instant.now().plus(10, ChronoUnit.MINUTES); // 10분으로 단축
        
        // 기존 인증 레코드가 있다면 삭제 (중복 방지)
        repo.findAllByEmail(email).forEach(repo::delete);
        
        // 새로운 인증 정보 저장
        EmailVerification ev = new EmailVerification(email, code, expiredDate);
        repo.save(ev);
        
        // 개발용 로그 출력 (테스트 시 인증 코드 확인용)
        System.out.println("=== 이메일 인증 코드 (개발용) ===");
        System.out.println("이메일: " + email);
        System.out.println("인증 코드: " + code);
        System.out.println("만료 시간: " + expiredDate);
        System.out.println("================================");
        
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("[TaleCraft] 이메일 인증 코드");
        mail.setText("안녕하세요!\n\n" +
                "TaleCraft 회원가입을 위한 이메일 인증 코드입니다.\n\n" +
                "인증 코드: " + code + "\n\n" +
                "이 코드는 10분간 유효합니다.\n" +
                "본인이 요청하지 않은 경우 이 메일을 무시하세요.\n\n" +
                "감사합니다.\n" +
                "TaleCraft 팀");
        mailSender.send(mail);
        
        return code;
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
        return repo.findFirstByEmailOrderByIdDesc(email)
                .map(EmailVerification::isVerified)
                .orElse(false);
    }

    /** 회원가입 완료 처리 (SIGNED_UP = 1로 변경) */
    public void markAsSignedUp(String email) {
        repo.findFirstByEmailOrderByIdDesc(email)
                .filter(EmailVerification::isVerified)
                .ifPresent(e -> {
                    e.setSignedUp(true);
                    repo.save(e);
                });
    }

    /** 이메일로 회원가입 완료 여부 확인 */
    public boolean isSignedUp(String email) {
        return repo.findFirstByEmailOrderByIdDesc(email)
                .map(EmailVerification::isSignedUp)
                .orElse(false);
    }
}

