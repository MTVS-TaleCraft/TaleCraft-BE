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
import java.util.Collections;
import java.util.stream.Collectors;

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
        
        logger.info("shouldNotFilter called for path: {} method: {}", path, method);

        if (path.startsWith("/swagger-ui/") || path.equals("/swagger-ui.html")) {
            logger.info("Excluding swagger-ui path");
            return true;
        }
        if (path.startsWith("/v3/api-docs/") || path.startsWith("/api-docs/") || path.equals("/api-docs")) {
            logger.info("Excluding api-docs path");
            return true;
        }
        
        // 로그인과 회원가입만 JWT 필터 제외
        if (path.equals("/api/auth/login") && "POST".equals(method)) {
            logger.info("Excluding login path");
            return true;
        }
        if (path.equals("/api/auth/signup") && "POST".equals(method)) {
            logger.info("Excluding signup path");
            return true;
        }
        if (path.equals("/api/auth/find-userid") && "POST".equals(method)) {
            logger.info("Excluding find-userid path");
            return true;
        }
        if (path.equals("/api/auth/find-password") && "POST".equals(method)) {
            logger.info("Excluding find-password path");
            return true;
        }

        
        // 이메일 인증 관련 경로 제외
        if (path.startsWith("/api/verification/")) {
            logger.info("Excluding verification path");
            return true;
        }
        
        // 소설 목록 조회 경로 제외 (인증 불필요) - /my 경로는 제외하지 않음
        if (path.equals("/api/novels") && "GET".equals(method)) {
            logger.info("Excluding novels list path");
            return true;
        }
        if (path.startsWith("/api/novels/") && "GET".equals(method) && !path.equals("/api/novels/my") && !path.contains("/bookmarks/") && !path.equals("/api/novels/bookmarks") && !path.contains("/like")) {
            logger.info("Excluding novel detail path");
            return true;
        }
        
        // 기본 태그 API 제외 (인증 불필요)
        if (path.equals("/api/tags/common") && "GET".equals(method)) {
            logger.info("Excluding common tags path");
            return true;
        }
        if (path.equals("/api/tags/default") && "GET".equals(method)) {
            logger.info("Excluding default tags path");
            return true;
        }
        if (path.startsWith("/api/tags/search/") && "GET".equals(method)) {
            logger.info("Excluding tag search path");
            return true;
        }
        
        logger.info("Path {} will be filtered", path);
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

        // 북마크 관련 경로에 대한 특별 로그
        if (req.getRequestURI().contains("/bookmarks/")) {
            logger.info("북마크 요청 감지: {}", req.getRequestURI());
            logger.info("요청 메서드: {}", req.getMethod());
            logger.info("모든 헤더: {}", Collections.list(req.getHeaderNames()).stream()
                .collect(Collectors.toMap(name -> name, req::getHeader)));
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
        Cookie[] cookies = request.getCookies();
        logger.info("All cookies: {}", cookies != null ? Arrays.stream(cookies).map(c -> c.getName() + "=" + c.getValue().substring(0, Math.min(c.getValue().length(), 10)) + "...").toList() : "null");
        
        if (cookies != null) {
            logger.info("Looking for cookie with name: {}", COOKIE_NAME);
            String cookieToken = Arrays.stream(cookies)
                    .filter(c -> {
                        boolean matches = COOKIE_NAME.equals(c.getName());
                        logger.info("Cookie {} matches {}: {}", c.getName(), COOKIE_NAME, matches);
                        return matches;
                    })
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




