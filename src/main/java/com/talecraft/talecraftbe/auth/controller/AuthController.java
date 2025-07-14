package com.talecraft.talecraftbe.auth.controller;

import com.talecraft.talecraftbe.auth.dto.LoginRequest;
import com.talecraft.talecraftbe.auth.dto.SignupRequest;
import com.talecraft.talecraftbe.auth.service.AuthService;
import com.talecraft.talecraftbe.verification.service.EmailVerificationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    public AuthController(AuthService authService,
                          EmailVerificationService emailVerificationService) {
        this.authService = authService;
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest req) {
        // 1) User 저장
        authService.signup(req);
        // 2) 이메일 인증 토큰 생성 및 메일 발송
        emailVerificationService.createAndSendToken(req.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody LoginRequest req,
            HttpServletResponse response
    ) {
        authService.login(req, response);
        return ResponseEntity.ok().build();
    }
}
