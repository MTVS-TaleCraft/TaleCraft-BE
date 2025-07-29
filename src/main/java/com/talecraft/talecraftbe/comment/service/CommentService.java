package com.talecraft.talecraftbe.comment.service;

import com.talecraft.talecraftbe.comment.dto.request.RequestPostCommentDto;
import com.talecraft.talecraftbe.comment.dto.request.RequestUpdateCommentDto;
import com.talecraft.talecraftbe.comment.dto.response.*;
import com.talecraft.talecraftbe.comment.exception.NovelNotFoundException;
import com.talecraft.talecraftbe.comment.model.entity.CommentEntity;
import com.talecraft.talecraftbe.comment.repository.CommentRepository;
import com.talecraft.talecraftbe.novel.model.entity.NovelEntity;
import com.talecraft.talecraftbe.novel.repository.NovelRepository;
import com.talecraft.talecraftbe.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final NovelRepository novelRepository;

    public CommentService(CommentRepository commentRepository, NovelRepository novelRepository) {
        this.commentRepository = commentRepository;
        this.novelRepository = novelRepository;
    }

    public ResponseEntity<ResponseGetCommentListDto> getAllComment(Long novelId, Pageable pageable) {
        try{
            NovelEntity novelEntity = novelRepository.findById(novelId).orElseThrow(() -> new NovelNotFoundException(novelId));
            Page<CommentEntity> commentPage = commentRepository.findByNovel(novelEntity, pageable);
            List<ResponseGetCommentItemDto> commentDtos = commentPage.stream()
                    .map(ResponseGetCommentItemDto::new)
                    .toList();

            ResponseGetCommentListDto responseDto = new ResponseGetCommentListDto(
                    commentDtos,
                    commentPage.getTotalPages(),
                    commentPage.getTotalElements()
            );

            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<ResponsePostCommentDto> addComment(RequestPostCommentDto requestPostCommentDto, User user) {
        try{
            CommentEntity commentEntity = CommentEntity.builder()
                    .content(requestPostCommentDto.getContent()).user(user).build();
            commentRepository.save(commentEntity);
            return new ResponseEntity<>(new ResponsePostCommentDto(commentEntity.getCommentId(),"댓글 등록 성공"), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResponseDeleteCommentDto> deleteComment(User user,long commentId) {
        try{
            CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow();
            commentEntity.updateDeleted(true);
            commentRepository.save(commentEntity);
            return new ResponseEntity<>(new ResponseDeleteCommentDto("삭제 성공"), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResponseUpdateCommentDto> updateComment(RequestUpdateCommentDto requestUpdateCommentDto, User user, long commentId) {
        try{
            CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow();
            commentEntity.updateComment(requestUpdateCommentDto);
            commentRepository.save(commentEntity);
            return new ResponseEntity<>(new ResponseUpdateCommentDto(commentId,"댓글 수정 성공"), HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }






}
