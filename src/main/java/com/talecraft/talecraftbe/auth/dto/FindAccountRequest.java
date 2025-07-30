package com.talecraft.talecraftbe.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FindAccountRequest(
        @Email @NotBlank String email
) {} 