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

import java.time.LocalDate;
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

    public ResponseEntity<ResponseGetCommentListDto> getAllComment(Long novelId, Long episodeId, Pageable pageable) {
        try{
            log.info("Getting comments for novel ID: {} and episode ID: {}", novelId, episodeId);
            NovelEntity novelEntity = novelRepository.findById(novelId).orElseThrow(() -> new NovelNotFoundException(novelId));
            log.info("Found novel: {}", novelEntity.getTitle());
            
            Page<CommentEntity> commentPage = commentRepository.findByNovelAndEpisodeId(novelEntity, episodeId, pageable);
            log.info("Found {} comments", commentPage.getTotalElements());
            
            List<ResponseGetCommentItemDto> commentDtos = commentPage.stream()
                    .map(ResponseGetCommentItemDto::new)
                    .toList();
            log.info("Converted to {} DTOs", commentDtos.size());

            ResponseGetCommentListDto responseDto = new ResponseGetCommentListDto(
                    commentDtos,
                    commentPage.getTotalPages(),
                    commentPage.getTotalElements()
            );
            log.info("Response DTO created: {}", responseDto);

            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error getting comments for novel {} and episode {}: ", novelId, episodeId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<ResponsePostCommentDto> addComment(Long novelId, Long episodeId, RequestPostCommentDto requestPostCommentDto, User user) {
        try{
            NovelEntity novelEntity = novelRepository.findById(novelId).orElseThrow(() -> new NovelNotFoundException(novelId));
            CommentEntity commentEntity = CommentEntity.builder()
                    .content(requestPostCommentDto.getContent())
                    .user(user)
                    .novel(novelEntity)
                    .episodeId(episodeId)
                    .createdDate(LocalDate.now())
                    .isDeleted(false)
                    .build();
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
