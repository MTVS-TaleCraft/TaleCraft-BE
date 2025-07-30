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
    private String message;
    private List<ChatListResponseDTO> chatList;

    public FindAIResponseDTO(List<ChatListResponseDTO> responseDTOList) {
        this.status = true;
        this.message = "성공!";
        this.chatList = responseDTOList;
    }
}
