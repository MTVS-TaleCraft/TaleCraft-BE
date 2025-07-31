package com.talecraft.talecraftbe.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private static final String COOKIE_NAME = "JwtToken";
    private final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        String path = req.getRequestURI();
        String method = req.getMethod();
        
        // 로그인과 회원가입만 JWT 필터 제외
        if (path.equals("/api/auth/login") && "POST".equals(method)) {
            return true;
        }
        if (path.equals("/api/auth/signup") && "POST".equals(method)) {
            return true;
        }
        
        // 이메일 인증 관련 경로 제외
        if (path.startsWith("/api/verification/")) {
            return true;
        }
        
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        // 인증이 필요하지 않은 경로는 바로 통과
        if (shouldNotFilter(req)) {
            chain.doFilter(req, res);
            return;
        }

        try {
            // JWT 토큰 추출 (쿠키 또는 Authorization 헤더에서)
            String token = extractToken(req);
            logger.info("Extracted token: {}", token != null ? token.substring(0, Math.min(token.length(), 20)) + "..." : "null");

            // 토큰이 있고 유효하면 인증 설정
            if (token != null && jwtProvider.validateToken(token)) {
                logger.info("Token is valid, setting authentication");
                Authentication auth = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.info("Authentication set successfully for user: {}", auth.getName());
            } else {
                logger.warn("Token is null or invalid for path: {}", req.getRequestURI());
                // 토큰이 없거나 유효하지 않으면 401 반환
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing token");
                return;
            }

            chain.doFilter(req, res);
        } catch (Exception e) {
            logger.error("Error in JWT filter: ", e);
            // 예외 발생 시 401 반환
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

    private String extractToken(HttpServletRequest request) {
        // 1. Authorization 헤더에서 Bearer 토큰 추출
        String authHeader = request.getHeader("Authorization");
        logger.info("Authorization header: {}", authHeader);
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logger.info("Extracted Bearer token: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
            return token;
        }

        // 2. 쿠키에서 토큰 추출
        if (request.getCookies() != null) {
            String cookieToken = Arrays.stream(request.getCookies())
                    .filter(c -> COOKIE_NAME.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
            logger.info("Extracted cookie token: {}", cookieToken != null ? cookieToken.substring(0, Math.min(cookieToken.length(), 20)) + "..." : "null");
            return cookieToken;
        }

        logger.info("No token found");
        return null;
    }
}




