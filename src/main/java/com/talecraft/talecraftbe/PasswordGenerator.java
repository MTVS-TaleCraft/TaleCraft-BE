package com.talecraft.talecraftbe;

public class PasswordGenerator {
    public static void main(String[] args) {
        String raw = "admin123";  // 원하는 관리자 비밀번호
        String hash = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(raw);
        System.out.println(hash);
    }
}
