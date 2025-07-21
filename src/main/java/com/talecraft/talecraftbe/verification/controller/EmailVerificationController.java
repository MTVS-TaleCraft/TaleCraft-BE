// src/main/java/.../verification/controller/EmailVerificationController.java
package com.talecraft.talecraftbe.verification.controller;

import com.talecraft.talecraftbe.verification.dto.EmailVerificationRequest;
import com.talecraft.talecraftbe.verification.dto.EmailVerificationResponse;
import com.talecraft.talecraftbe.verification.service.EmailVerificationService;
import com.talecraft.talecraftbe.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification")
public class EmailVerificationController {

    private final EmailVerificationService service;
    private final AuthService authService;

    public EmailVerificationController(EmailVerificationService service, AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    @PostMapping("/send")
    public ResponseEntity<EmailVerificationResponse> send(
            @RequestBody @Valid EmailVerificationRequest req) {

        service.createAndSendToken(req.email(), req.userName(), req.password());
        return ResponseEntity.ok(new EmailVerificationResponse(true, "인증 메일 발송됨"));
    }

    @GetMapping
    public ResponseEntity<EmailVerificationResponse> verify(
            @RequestParam String code,
            @RequestParam String email) {

        boolean verified = service.verify(code, email);
        if (verified) {
            // 인증 완료 후 실제 회원가입 처리
            var signupInfo = service.getVerifiedSignupInfo(code, email);
            if (signupInfo != null) {
                authService.completeSignup(signupInfo.getEmail(), signupInfo.getUserName(), signupInfo.getPassword());
                return ResponseEntity.ok(new EmailVerificationResponse(true, "인증 완료 및 회원가입이 완료되었습니다."));
            }
            return ResponseEntity.ok(new EmailVerificationResponse(true, "인증 완료"));
        }
        return ResponseEntity
                .badRequest()
                .body(new EmailVerificationResponse(false, "토큰 유효하지 않거나 만료됨"));
    }
}

