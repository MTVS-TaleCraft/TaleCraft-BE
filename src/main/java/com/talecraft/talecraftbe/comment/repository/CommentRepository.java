package com.talecraft.talecraftbe.comment.repository;

import com.talecraft.talecraftbe.comment.model.entity.CommentEntity;
import com.talecraft.talecraftbe.novel.model.entity.NovelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity,Long> {
    List<CommentEntity> findCommentEntitiesByNovel(NovelEntity novel);
    Page<CommentEntity> findByNovel(NovelEntity novel, Pageable pageable);

}
