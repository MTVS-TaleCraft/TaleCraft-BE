package com.talecraft.talecraftbe.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FindPasswordRequest(
        @Email @NotBlank String email,
        @NotBlank String userId
) {} 