package com.talecraft.talecraftbe.auth.dto;

public record UpdateUserRequest(
        String userName,
        String email,
        String currentPassword,
        String newPassword
) {} 