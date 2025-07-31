package com.talecraft.talecraftbe.tag.repository;

import com.talecraft.talecraftbe.tag.model.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Tag.TagId> {
    
    // 작품별 태그 목록 조회
    List<Tag> findByNovelId(Long novelId);
    
    // 태그명으로 작품 검색
    @Query("SELECT DISTINCT t.novelId FROM Tag t WHERE t.tagName LIKE %:tagName%")
    List<Long> findNovelIdsByTagNameContaining(@Param("tagName") String tagName);
    
    // 특정 작품의 특정 태그 존재 여부 확인
    boolean existsByNovelIdAndTagName(Long novelId, String tagName);
    
    // 태그명으로 태그 검색
    List<Tag> findByTagNameContaining(String tagName);
    
    // 작품별 태그 개수 조회
    @Query("SELECT COUNT(t) FROM Tag t WHERE t.novelId = :novelId")
    long countByNovelId(@Param("novelId") Long novelId);
} 