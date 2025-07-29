package com.talecraft.talecraftbe.comment.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseUpdateCommentDto {
    long comment;
    String message;
}
