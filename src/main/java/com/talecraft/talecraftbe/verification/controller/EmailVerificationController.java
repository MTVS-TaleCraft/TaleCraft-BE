// src/main/java/.../verification/controller/EmailVerificationController.java
package com.talecraft.talecraftbe.verification.controller;

import com.talecraft.talecraftbe.verification.dto.EmailVerificationRequest;
import com.talecraft.talecraftbe.verification.dto.EmailVerificationResponse;
import com.talecraft.talecraftbe.verification.service.EmailVerificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification")
public class EmailVerificationController {

    private final EmailVerificationService service;

    public EmailVerificationController(EmailVerificationService service) {
        this.service = service;
    }

    @PostMapping("/send")
    public ResponseEntity<EmailVerificationResponse> send(
            @RequestBody @Valid EmailVerificationRequest req) {

        service.createAndSendToken(req.email());
        return ResponseEntity.ok(new EmailVerificationResponse(true, "인증 코드가 이메일로 발송되었습니다."));
    }

    @PostMapping("/verify")
    public ResponseEntity<EmailVerificationResponse> verify(
            @RequestBody @Valid EmailVerificationRequest req) {

        boolean verified = service.verify(req.code(), req.email());
        if (verified) {
            return ResponseEntity.ok(new EmailVerificationResponse(true, "이메일 인증이 완료되었습니다."));
        }
        return ResponseEntity
                .badRequest()
                .body(new EmailVerificationResponse(false, "인증 코드가 유효하지 않거나 만료되었습니다."));
    }
}

