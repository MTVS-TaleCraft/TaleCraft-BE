package com.talecraft.talecraftbe.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class ResponsePostCommentDto {
    long commentId;
    String message;
}
