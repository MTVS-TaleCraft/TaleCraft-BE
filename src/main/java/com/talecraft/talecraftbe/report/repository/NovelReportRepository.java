package com.talecraft.talecraftbe.report.repository;

import com.talecraft.talecraftbe.report.entity.NovelReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface NovelReportRepository extends JpaRepository<NovelReport, Long> {
    
    // 특정 작품의 모든 신고 조회
    List<NovelReport> findByNovelIdOrderByReportDateDesc(Long novelId);
    
    // 특정 사용자가 신고한 목록 조회
    List<NovelReport> findByReportUserOrderByReportDateDesc(String reportUser);
    
    // 특정 사용자가 신고받은 목록 조회
    List<NovelReport> findByReportedUserOrderByReportDateDesc(String reportedUser);
    
    // 확인되지 않은 신고 목록 조회
    List<NovelReport> findByIsViewFalseOrderByReportDateDesc();
    
    // 특정 작품에 대한 특정 사용자의 신고 여부 확인
    boolean existsByNovelIdAndReportUser(Long novelId, String reportUser);
    
    // 특정 작품의 신고 개수 조회
    @Query("SELECT COUNT(nr) FROM NovelReport nr WHERE nr.novelId = :novelId")
    long countByNovelId(@Param("novelId") Long novelId);
    
    // 신고된 소설의 고유 ID 목록 조회
    @Query("SELECT DISTINCT nr.novelId FROM NovelReport nr")
    List<Long> findDistinctNovelIds();
} 