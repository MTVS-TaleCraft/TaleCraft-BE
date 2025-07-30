package com.talecraft.talecraftbe.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageSendRequest(
        @NotBlank(message = "받는 사람을 입력해주세요")
        @Size(max = 20, message = "받는 사람은 20자 이하여야 합니다")
        String receiver,
        
        @NotBlank(message = "제목을 입력해주세요")
        @Size(max = 255, message = "제목은 255자 이하여야 합니다")
        String messageTitle,
        
        @NotBlank(message = "내용을 입력해주세요")
        String description
) {} 