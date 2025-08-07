package com.talecraft.talecraftbe.admin.controller;

import com.talecraft.talecraftbe.auth.dto.UserDetailResponse;
import com.talecraft.talecraftbe.auth.service.AuthService;
import com.talecraft.talecraftbe.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 특정 사용자 정보 조회 (관리자용)
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserDetail(@PathVariable String userId, @AuthenticationPrincipal User user) {
        try {
            // 관리자 권한 확인은 AuthService에서 처리
            UserDetailResponse userDetail = authService.getUserDetail(userId);
            return ResponseEntity.ok(userDetail);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "사용자 정보 조회에 실패했습니다."));
        }
    }

    /**
     * 전체 사용자 목록 조회 (관리자용)
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDetailResponse>> getAllUsers(@AuthenticationPrincipal User user) {
        try {
            List<UserDetailResponse> users = authService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 