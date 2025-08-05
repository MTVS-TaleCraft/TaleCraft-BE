package com.talecraft.talecraftbe.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtFilter jwtFilter(JwtProvider jwtProvider) {
        return new JwtFilter(jwtProvider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "https://tale-craft-three.vercel.app",
                "https://tale-craft-cnbbfdayr-emflazlwm62-9053s-projects.vercel.app",
                "http://localhost:3000",  // 로컬 개발용
                "http://localhost:8080",  // 필요하다면 추가
                "http://localhost:8081"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(cs -> cs.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1) 회원가입·로그인 (인증 불필요)
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/find-userid").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/find-password").permitAll()

                        // 2) 이메일 인증 API
                        .requestMatchers(HttpMethod.POST, "/api/verification/send").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/verification/verify").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/verification").permitAll()

                        // 3) 퍼블릭 리소스
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/api/novels/**").permitAll()
                        .requestMatchers("/api/novels").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tags/common").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tags/default").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tags/search/**").permitAll()

                        // 3-1) swagger 리소스
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()

                        // 4) 나머지 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // JWT 검증 필터 (permitAll 경로는 스킵됨)
                .addFilterBefore(jwtFilter, BasicAuthenticationFilter.class);

        return http.build();
    }
}


