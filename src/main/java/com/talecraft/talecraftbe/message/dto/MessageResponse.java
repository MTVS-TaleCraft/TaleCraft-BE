package com.talecraft.talecraftbe.message.dto;

public record MessageResponse(
        Long messageId,
        String sender,
        String receiver,
        String messageTitle,
        String description,
        Boolean isSenderDeleted,
        Boolean isReceiverDeleted
) {} 