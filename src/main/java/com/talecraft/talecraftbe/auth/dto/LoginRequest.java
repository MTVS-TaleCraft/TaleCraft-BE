package com.talecraft.talecraftbe.auth.dto;

public record LoginRequest(
        String email,
        String password
) {}
