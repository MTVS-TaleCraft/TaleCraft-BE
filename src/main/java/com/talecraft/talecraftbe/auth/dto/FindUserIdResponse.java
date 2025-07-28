package com.talecraft.talecraftbe.auth.dto;

public record FindUserIdResponse(
        boolean success,
        String message,
        String userId
) {} 