package com.talecraft.talecraftbe.comment.dto.response;

import com.talecraft.talecraftbe.comment.model.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ResponseGetCommentListDto {
    private List<ResponseGetCommentItemDto> comments;
    private int totalPages;
    private long totalElements;
}
