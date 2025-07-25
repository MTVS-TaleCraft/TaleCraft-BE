package com.talecraft.talecraftbe.comment.controller;


import com.talecraft.talecraftbe.comment.dto.response.ResponseGetCommentItemDto;
import com.talecraft.talecraftbe.comment.dto.response.ResponseGetCommentListDto;
import com.talecraft.talecraftbe.comment.service.CommentService;
import com.talecraft.talecraftbe.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/novels/{novelId}/episodes/{episodeId}/comments")
    public ResponseEntity<ResponseGetCommentListDto> getAllComments(@PathVariable long novelId, @AuthenticationPrincipal User user, @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
    Pageable pageable) {
        return commentService.getAllComment(novelId,pageable);
    }

    @PostMapping
    public
}
