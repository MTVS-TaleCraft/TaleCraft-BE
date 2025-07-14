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
        return ResponseEntity.ok(new EmailVerificationResponse(true, "인증 메일 발송됨"));
    }

    @GetMapping
    public ResponseEntity<EmailVerificationResponse> verify(
            @RequestParam String code) {

        boolean ok = service.verify(code);
        if (ok) {
            return ResponseEntity.ok(new EmailVerificationResponse(true, "인증 완료"));
        }
        return ResponseEntity
                .badRequest()
                .body(new EmailVerificationResponse(false, "토큰 유효하지 않거나 만료됨"));
    }
}

