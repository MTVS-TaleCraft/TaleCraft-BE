package com.talecraft.talecraftbe.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Arrays;

public class JwtFilter extends OncePerRequestFilter {

    private static final String COOKIE_NAME = "JwtToken";
    private final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        String path = req.getRequestURI();
        return path.startsWith("/api/auth/") || path.startsWith("/api/verification/");
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
            // JWT 토큰 추출
            String token = Arrays.stream(req.getCookies() != null ? req.getCookies() : new Cookie[0])
                    .filter(c -> COOKIE_NAME.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            // 토큰이 있고 유효하면 인증 설정
            if (token != null && jwtProvider.validateToken(token)) {
                Authentication auth = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            chain.doFilter(req, res);
        } catch (Exception e) {
            // 예외 발생 시 401 반환
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }
}




