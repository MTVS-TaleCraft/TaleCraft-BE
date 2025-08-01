package com.talecraft.talecraftbe.user.controller;

import com.talecraft.talecraftbe.user.service.UserService;
import com.talecraft.talecraftbe.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<?> withdrawUser(@AuthenticationPrincipal User user) {
        try {
            log.info("회원탈퇴 요청: userId={}", user.getId());
            
            userService.withdrawUser(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", true);
            response.put("message", "회원탈퇴가 완료되었습니다.");
            
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            log.error("회원탈퇴 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("회원탈퇴 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "회원탈퇴 처리 중 오류가 발생했습니다."));
        }
    }
} 