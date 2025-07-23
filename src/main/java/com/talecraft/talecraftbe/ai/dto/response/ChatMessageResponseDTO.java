package com.talecraft.talecraftbe.ai.dto.response;

import com.talecraft.talecraftbe.ai.model.entity.ChatMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatMessageResponseDTO {
    private String request;
    private String response;

    public ChatMessageResponseDTO(ChatMessage chatMessage) {
        this.request = chatMessage.getQuestionMessage();
        this.response = chatMessage.getResponseMessage();
    }
}
