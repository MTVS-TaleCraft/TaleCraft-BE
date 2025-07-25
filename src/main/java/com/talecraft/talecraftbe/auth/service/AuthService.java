package com.talecraft.talecraftbe.auth.service;

import com.talecraft.talecraftbe.auth.dto.LoginRequest;
import com.talecraft.talecraftbe.auth.dto.SignupRequest;
import com.talecraft.talecraftbe.auth.dto.UpdateUserRequest;
import com.talecraft.talecraftbe.user.entity.User;
import com.talecraft.talecraftbe.user.repository.UserRepository;
import com.talecraft.talecraftbe.auth.config.JwtProvider;
import com.talecraft.talecraftbe.verification.service.EmailVerificationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtProvider jwtProvider;
    private final EmailVerificationService emailVerificationService;

    public AuthService(UserRepository userRepo,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authManager,
                       JwtProvider jwtProvider,
                       EmailVerificationService emailVerificationService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtProvider = jwtProvider;
        this.emailVerificationService = emailVerificationService;
    }

    /**
     * 회원가입: 이메일 인증 확인 후 USERS 테이블에 저장하고 SIGNED_UP = 1로 변경
     */
    public void signup(SignupRequest req) {
        // 아이디 중복 검사
        if (userRepo.existsById(req.userId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        
        // 이메일 중복 검사
        if (userRepo.existsByEmail(req.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        
        // 이메일 인증이 완료되었는지 확인
        if (!emailVerificationService.isEmailVerified(req.email())) {
            throw new IllegalArgumentException("이메일 인증이 필요합니다. 먼저 이메일 인증을 완료해주세요.");
        }
        
        // 이미 회원가입이 완료되었는지 확인
        if (emailVerificationService.isSignedUp(req.email())) {
            throw new IllegalArgumentException("이미 회원가입이 완료된 이메일입니다.");
        }
        
        // USERS 테이블에 회원 정보 저장 (authority_id = 1)
        User user = new User();
        user.setId(req.userId());
        user.setUserName(req.userName());
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setAuthorityId(1L); // AUTHORITIES 테이블의 ID=1 (일반 사용자)
        userRepo.save(user);
        
        // 회원가입 완료 처리 (SIGNED_UP = 1)
        emailVerificationService.markAsSignedUp(req.email());
    }

    /**
     * 로그인: 아이디/비밀번호 인증, JWT 생성 후 쿠키에 담아 응답
     */
    public String login(LoginRequest req, HttpServletResponse response) {
        logger.info("Starting login process for userId: {}", req.userId());
        
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(req.userId(), req.password());
        logger.info("Created authentication token");
        
        Authentication auth = authManager.authenticate(authToken);
        logger.info("Authentication successful");
        
        SecurityContextHolder.getContext().setAuthentication(auth);
        logger.info("Security context set");

        String jwt = jwtProvider.generateToken(auth);
        logger.info("JWT token generated: {}", jwt.substring(0, Math.min(jwt.length(), 20)) + "...");
        
        Cookie cookie = new Cookie("JwtToken", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        logger.info("JWT cookie set");
        
        return jwt;
    }

    /**
     * 로그아웃: JWT 쿠키 제거 및 SecurityContext 초기화
     */
    public void logout(HttpServletResponse response) {
        // 현재 로그인된 사용자 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        logger.info("Logout for user: {}", authentication.getName());
        
        // SecurityContext 초기화
        SecurityContextHolder.clearContext();
        
        // JWT 쿠키 제거
        Cookie cookie = new Cookie("JwtToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 즉시 만료
        response.addCookie(cookie);
        
        logger.info("Logout completed successfully");
    }

    /**
     * 프로필 수정: 현재 로그인된 사용자의 정보 수정
     */
    public void updateProfile(UpdateUserRequest req) {
        // 현재 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String currentUserId = userDetails.getUsername(); // User 엔티티에서 getUsername()은 이제 id를 반환
        
        User currentUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 현재 비밀번호 확인
        if (req.currentPassword() != null && !req.currentPassword().isEmpty()) {
            if (!passwordEncoder.matches(req.currentPassword(), currentUser.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
        }
        
        // 이메일 중복 검사 (다른 사용자가 사용 중인지 확인)
        if (req.email() != null && !req.email().isEmpty() && !req.email().equals(currentUser.getEmail())) {
            if (userRepo.existsByEmail(req.email())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
        }
        
        // 사용자 정보 업데이트
        if (req.userName() != null && !req.userName().isEmpty()) {
            currentUser.setUserName(req.userName());
        }
        
        if (req.email() != null && !req.email().isEmpty()) {
            currentUser.setEmail(req.email());
        }
        
        if (req.newPassword() != null && !req.newPassword().isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(req.newPassword()));
        }
        
        userRepo.save(currentUser);
    }
}

