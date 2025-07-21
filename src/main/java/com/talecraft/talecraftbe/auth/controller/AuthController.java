package com.talecraft.talecraftbe.auth.controller;

import com.talecraft.talecraftbe.auth.dto.LoginRequest;
import com.talecraft.talecraftbe.auth.dto.SignupRequest;
import com.talecraft.talecraftbe.auth.service.AuthService;
import com.talecraft.talecraftbe.verification.dto.EmailVerificationRequest;
import com.talecraft.talecraftbe.verification.service.EmailVerificationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    public AuthController(AuthService authService, EmailVerificationService emailVerificationService) {
        this.authService = authService;
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest req) {
        // 이메일 인증 요청으로 변경
        EmailVerificationRequest verificationReq = new EmailVerificationRequest(
                req.email(), req.userName(), req.password());
        emailVerificationService.createAndSendToken(
                verificationReq.email(), 
                verificationReq.userName(), 
                verificationReq.password());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody LoginRequest req,
            HttpServletResponse response    // ← 이 파라미터를 추가합니다.
    ) {
        authService.login(req, response);
        return ResponseEntity.ok().build();
    }
}
