package com.talecraft.talecraftbe.verification.dto;

public record EmailVerificationResponse(
        boolean success,
        String message
) {}
