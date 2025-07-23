package com.talecraft.talecraftbe.auth.dto;

public record LoginRequest(
        String userId,
        String password
) {}
