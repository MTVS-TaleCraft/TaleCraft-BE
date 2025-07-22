package com.talecraft.talecraftbe.auth.service;

import com.talecraft.talecraftbe.auth.dto.LoginRequest;
import com.talecraft.talecraftbe.auth.dto.SignupRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
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
        user.setUserName(req.userName());
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setAuthorityId(1L); // AUTHORITIES 테이블의 ID=1 (일반 사용자)
        userRepo.save(user);
        
        // 회원가입 완료 처리 (SIGNED_UP = 1)
        emailVerificationService.markAsSignedUp(req.email());
    }

    /**
     * 로그인: 이메일/비밀번호 인증, JWT 생성 후 쿠키에 담아 응답
     */
    public void login(LoginRequest req, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(req.email(), req.password());
        Authentication auth = authManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = jwtProvider.generateToken(auth);
        Cookie cookie = new Cookie("JwtToken", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}

