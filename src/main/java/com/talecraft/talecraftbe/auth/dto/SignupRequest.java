package com.talecraft.talecraftbe.auth.dto;

public record SignupRequest(
        String userId,
        String userName,
        String email,
        String password
) {}
