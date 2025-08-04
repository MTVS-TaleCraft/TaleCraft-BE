package com.talecraft.talecraftbe.tag.repository;

import com.talecraft.talecraftbe.tag.model.entity.NovelTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NovelTagRepository extends JpaRepository<NovelTag, NovelTag.NovelTagId> {
    
    // 작품별 태그 ID 목록 조회
    @Query("SELECT nt.tagId FROM NovelTag nt WHERE nt.novelId = :novelId")
    List<Long> findTagIdsByNovelId(@Param("novelId") Long novelId);
    
    // 태그 ID로 작품 ID 목록 조회
    @Query("SELECT nt.novelId FROM NovelTag nt WHERE nt.tagId = :tagId")
    List<Long> findNovelIdsByTagId(@Param("tagId") Long tagId);
    
    // 태그명으로 작품 검색 (JOIN 사용)
    @Query("SELECT DISTINCT nt.novelId FROM NovelTag nt JOIN Tag t ON nt.tagId = t.tagId WHERE t.tagName LIKE %:tagName%")
    List<Long> findNovelIdsByTagNameContaining(@Param("tagName") String tagName);
    
    // 특정 작품의 특정 태그 존재 여부 확인
    @Query("SELECT COUNT(nt) > 0 FROM NovelTag nt JOIN Tag t ON nt.tagId = t.tagId WHERE nt.novelId = :novelId AND t.tagName = :tagName")
    boolean existsByNovelIdAndTagName(@Param("novelId") Long novelId, @Param("tagName") String tagName);
    
    // 작품별 태그 개수 조회
    @Query("SELECT COUNT(nt) FROM NovelTag nt WHERE nt.novelId = :novelId")
    long countByNovelId(@Param("novelId") Long novelId);
    
    // 작품의 모든 태그 매핑 삭제
    void deleteByNovelId(Long novelId);
    
    // 특정 작품의 특정 태그 매핑 삭제 (서브쿼리 사용)
    @Modifying
    @Query("DELETE FROM NovelTag nt WHERE nt.novelId = :novelId AND nt.tagId IN (SELECT t.tagId FROM Tag t WHERE t.tagName = :tagName)")
    void deleteByNovelIdAndTagName(@Param("novelId") Long novelId, @Param("tagName") String tagName);
} 