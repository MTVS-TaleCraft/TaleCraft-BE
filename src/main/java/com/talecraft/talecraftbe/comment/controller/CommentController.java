package com.talecraft.talecraftbe.comment.controller;


import com.talecraft.talecraftbe.comment.dto.request.RequestPostCommentDto;
import com.talecraft.talecraftbe.comment.dto.request.RequestUpdateCommentDto;
import com.talecraft.talecraftbe.comment.dto.response.*;
import com.talecraft.talecraftbe.comment.service.CommentService;
import com.talecraft.talecraftbe.novel.episode.dto.request.RequestUpdateEpisodeDto;
import com.talecraft.talecraftbe.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<ResponseGetCommentListDto> getAllComments(@PathVariable long novelId, @PathVariable long episodeId, @AuthenticationPrincipal User user, @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC)
    Pageable pageable) {
        return commentService.getAllComment(novelId, episodeId, pageable);
    }

    @PostMapping("/novels/{novelId}/episodes/{episodeId}/comments")
    public ResponseEntity<ResponsePostCommentDto> addComment(@PathVariable long novelId, @PathVariable long episodeId, @RequestBody RequestPostCommentDto requestPostCommentDto, @AuthenticationPrincipal User user) {
        return commentService.addComment(novelId, episodeId, requestPostCommentDto, user);
    }


    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ResponseDeleteCommentDto> deleteComment(@AuthenticationPrincipal User user,@PathVariable long commentId) {
        return commentService.deleteComment(user,commentId);
    }

    @PostMapping("/comments/{commentId}")
    public ResponseEntity<ResponseUpdateCommentDto> updateComment(@RequestBody RequestUpdateCommentDto requestUpdateCommentDto, @AuthenticationPrincipal User user, @PathVariable long commentId) {
        return commentService.updateComment(requestUpdateCommentDto,user,commentId);
    }


}
