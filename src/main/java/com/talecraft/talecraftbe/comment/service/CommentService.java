package com.talecraft.talecraftbe.comment.service;

import com.talecraft.talecraftbe.comment.dto.request.RequestPostCommentDto;
import com.talecraft.talecraftbe.comment.dto.response.ResponseGetCommentItemDto;
import com.talecraft.talecraftbe.comment.dto.response.ResponseGetCommentListDto;
import com.talecraft.talecraftbe.comment.dto.response.ResponsePostCommentDto;
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
    }


    public ResponseEntity<ResponsePostCommentDto> addComment(RequestPostCommentDto requestPostCommentDto, User user) {
        CommentEntity commentEntity = CommentEntity.builder()
                                                    .user(user)
                                                    .content(requestPostCommentDto.getContent())
                                                    .novel();



    }

*/

}
