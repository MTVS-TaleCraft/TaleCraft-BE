package com.talecraft.talecraftbe.bookmark.repository;

import com.talecraft.talecraftbe.bookmark.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    
    // 사용자별 북마크 목록 조회
    List<Bookmark> findAllByUserId(String userId);
    
    // 특정 사용자의 특정 작품 북마크 조회
    Bookmark findByUserIdAndNovel_NovelId(String userId, Long novelId);
    
    // 북마크 존재 여부 확인
    boolean existsByUserIdAndNovel_NovelId(String userId, Long novelId);
}
