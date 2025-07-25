package com.talecraft.talecraftbe.ai.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class FindAIResponseDTO {
    private boolean status;
    private List<ChatMessageResponseDTO> chatMessages;

    public FindAIResponseDTO(List<ChatMessageResponseDTO> responseDTOList) {
        this.status = true;
        this.chatMessages = responseDTOList;
    }
}
