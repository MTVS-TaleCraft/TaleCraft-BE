package com.talecraft.talecraftbe.auth.controller;

import com.talecraft.talecraftbe.auth.dto.LoginRequest;
import com.talecraft.talecraftbe.auth.dto.SignupRequest;
import com.talecraft.talecraftbe.auth.dto.UpdateUserRequest;
import com.talecraft.talecraftbe.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody @Valid SignupRequest req) {
        try {
            authService.signup(req);
            return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginRequest req, 
                                                     HttpServletResponse response) {
        logger.info("Login request received for userId: {}", req.userId());
        try {
            String token = authService.login(req, response);
            logger.info("Login successful for userId: {}", req.userId());
            return ResponseEntity.ok(Map.of(
                "message", "로그인이 완료되었습니다.",
                "token", token
            ));
        } catch (Exception e) {
            logger.error("Login failed for userId: {}", req.userId(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "로그인에 실패했습니다."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        try {
            authService.logout(response);
            return ResponseEntity.ok(Map.of("message", "로그아웃이 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "로그아웃에 실패했습니다."));
        }
    }

    @PatchMapping("/profile")
    public ResponseEntity<Map<String, String>> updateProfile(@RequestBody @Valid UpdateUserRequest req) {
        try {
            authService.updateProfile(req);
            return ResponseEntity.ok(Map.of("message", "프로필이 성공적으로 수정되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "프로필 수정에 실패했습니다."));
        }
    }
}
