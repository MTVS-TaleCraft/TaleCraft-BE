package com.talecraft.talecraftbe.ai.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ChatListResponseDTO {
    private Long chatListId;
    private List<ChatMessageResponseDTO> chatMessages;

    public ChatListResponseDTO(Long chatListId, List<ChatMessageResponseDTO> chatMessages) {
        this.chatListId = chatListId;
        this.chatMessages = chatMessages;
    }
}
