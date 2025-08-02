package com.talecraft.talecraftbe.comment.dto.response;

import com.talecraft.talecraftbe.comment.model.entity.CommentEntity;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ResponseGetCommentItemDto {
    private Long commentId;
    private String userName;
    private String content;
    private LocalDate createdDate;
    private String userId;

    public ResponseGetCommentItemDto(CommentEntity commentEntity) {
        this.commentId = commentEntity.getCommentId();
        this.userName = commentEntity.getUser().getUsername();
        this.content = commentEntity.getContent();
        this.createdDate = commentEntity.getCreatedDate();
        this.userId = commentEntity.getUser().getId();
    }
}
