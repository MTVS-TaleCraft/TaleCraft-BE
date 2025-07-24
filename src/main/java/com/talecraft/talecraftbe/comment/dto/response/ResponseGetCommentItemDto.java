package com.talecraft.talecraftbe.comment.dto.response;

import com.talecraft.talecraftbe.comment.model.entity.CommentEntity;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ResponseGetCommentItemDto {
    private String userName;
    private String content;
    private LocalDate createdDate;

    public ResponseGetCommentItemDto(CommentEntity commentEntity) {
        this.userName = commentEntity.getUser().getUsername();
        this.content = commentEntity.getContent();
        this.createdDate = commentEntity.getCreatedDate();
    }
}
