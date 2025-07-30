// src/main/java/.../verification/dto/EmailVerificationRequest.java
package com.talecraft.talecraftbe.verification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailVerificationRequest(
        @Email @NotBlank String email,
        String code
) {}

