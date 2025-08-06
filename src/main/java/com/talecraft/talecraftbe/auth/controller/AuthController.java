package com.talecraft.talecraftbe.auth.controller;

import com.talecraft.talecraftbe.auth.dto.LoginRequest;
import com.talecraft.talecraftbe.auth.dto.SignupRequest;
import com.talecraft.talecraftbe.auth.dto.UpdateUserRequest;
import com.talecraft.talecraftbe.auth.dto.FindUserIdRequest;
import com.talecraft.talecraftbe.auth.dto.FindPasswordRequest;
import com.talecraft.talecraftbe.auth.dto.FindUserIdResponse;
import com.talecraft.talecraftbe.auth.dto.FindAccountResponse;
import com.talecraft.talecraftbe.auth.dto.UserDetailResponse;
import com.talecraft.talecraftbe.auth.service.AuthService;
import com.talecraft.talecraftbe.novel.dto.response.ResponseGetNovelListDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.List;

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

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam(required = false) String targetUserId) {
        try {
            // 관리자가 특정 사용자 정보를 조회하는 경우
            if (targetUserId != null && !targetUserId.isEmpty()) {
                UserDetailResponse userDetail = authService.getUserDetail(targetUserId);
                return ResponseEntity.ok(userDetail);
            }
            
            // 일반적인 현재 사용자 정보 조회
            Map<String, String> userInfo = authService.getCurrentUserInfo();
            return ResponseEntity.ok(userInfo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "사용자 정보 조회에 실패했습니다."));
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

    @PostMapping("/find-userid")
    public ResponseEntity<FindUserIdResponse> findUserId(@RequestBody @Valid FindUserIdRequest req) {
        try {
            String userId = authService.findUserId(req);
            return ResponseEntity.ok(new FindUserIdResponse(true, "아이디를 찾았습니다.", userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new FindUserIdResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new FindUserIdResponse(false, "아이디 찾기에 실패했습니다.", null));
        }
    }

    @PostMapping("/find-password")
    public ResponseEntity<FindAccountResponse> findPassword(@RequestBody @Valid FindPasswordRequest req) {
        try {
            authService.findPassword(req);
            return ResponseEntity.ok(new FindAccountResponse(true, "임시 비밀번호가 이메일로 발송되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new FindAccountResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new FindAccountResponse(false, "비밀번호 찾기에 실패했습니다."));
        }
    }

    @GetMapping("/profile/users")
    public ResponseEntity<List<UserDetailResponse>> getAllUsers() {
        try {
            List<UserDetailResponse> users = authService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 관리자용 소설 조회 (차단된 소설 포함)
    @GetMapping("/admin/novels")
    public ResponseEntity<ResponseGetNovelListDto> getAllNovelsForAdmin(@RequestParam(value = "value",required = false) String keyword,@RequestParam(value="type", required=false) String type) {
        try {
            ResponseGetNovelListDto novels = authService.getNovelListForAdmin(keyword, type);
            return ResponseEntity.ok(novels);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 소설 차단/해제 (관리자용)
    @PatchMapping("/admin/novels/{novelId}/ban")
    public ResponseEntity<?> toggleNovelBan(@PathVariable long novelId) {
        try {
            return authService.toggleNovelBan(novelId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "소설 차단/해제에 실패했습니다."));
        }
    }

}
