// src/main/java/.../verification/entity/EmailVerification.java
package com.talecraft.talecraftbe.verification.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "email_verification")
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "expired_date", nullable = false)
    private Instant expiredDate;

    @Column(name = "verification_code", nullable = false, unique = true, length = 255)
    private String verificationCode;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(name = "signed_up", nullable = false)
    private boolean signedUp = false;

    // 회원가입 정보를 임시 저장할 필드들
    @Column(name = "user_name", length = 255)
    private String userName;

    @Column(name = "password", length = 255)
    private String password;

    protected EmailVerification() {}

    public EmailVerification(String email, String verificationCode, Instant expiredDate) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.expiredDate = expiredDate;
    }

    public EmailVerification(String email, String verificationCode, Instant expiredDate, String userName, String password) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.expiredDate = expiredDate;
        this.userName = userName;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Instant expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isSignedUp() {
        return signedUp;
    }

    public void setSignedUp(boolean signedUp) {
        this.signedUp = signedUp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

