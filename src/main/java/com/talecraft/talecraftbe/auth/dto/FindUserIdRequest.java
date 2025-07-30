package com.talecraft.talecraftbe.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FindUserIdRequest(
        @Email @NotBlank String email
) {} 