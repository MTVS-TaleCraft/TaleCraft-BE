package com.talecraft.talecraftbe.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;
    private final long tokenValidityInMs;
    private final UserDetailsService userDetailsService;

    // ← 생성자는 클래스 바디 내부에 선언해야 합니다.
    public JwtProvider(UserDetailsService userDetailsService,
                       @Value("${security.jwt.secret-key}") String secretKey,
                       @Value("${security.jwt.token-validity-ms}") long tokenValidityInMs) {
        this.userDetailsService = userDetailsService;
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.tokenValidityInMs = tokenValidityInMs;
    }

    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + tokenValidityInMs);
        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }
}

