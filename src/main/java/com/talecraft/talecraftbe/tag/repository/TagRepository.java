package com.talecraft.talecraftbe.tag.repository;

import com.talecraft.talecraftbe.tag.model.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    // 태그명으로 태그 검색
    List<Tag> findByTagNameContaining(String tagName);
    
    // 태그명으로 태그 존재 여부 확인
    Optional<Tag> findByTagName(String tagName);
    
    // 태그명으로 태그 존재 여부 확인 (boolean)
    boolean existsByTagName(String tagName);
    
    // 모든 태그 목록 조회 (기본 태그들)
    List<Tag> findAllByOrderByTagNameAsc();
    
    // 태그명으로 태그 삭제
    @Modifying
    void deleteByTagName(String tagName);
} 