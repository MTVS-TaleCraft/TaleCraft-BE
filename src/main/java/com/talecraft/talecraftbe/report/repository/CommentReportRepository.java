package com.talecraft.talecraftbe.report.repository;

import com.talecraft.talecraftbe.report.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    
    // 특정 댓글의 모든 신고 조회
    List<CommentReport> findByCommentIdOrderByReportDateDesc(Long commentId);
    
    // 특정 사용자가 신고한 목록 조회
    List<CommentReport> findByReportUserOrderByReportDateDesc(String reportUser);
    
    // 특정 사용자가 신고받은 목록 조회
    List<CommentReport> findByReportedUserOrderByReportDateDesc(String reportedUser);
    
    // 확인되지 않은 신고 목록 조회
    List<CommentReport> findByIsViewFalseOrderByReportDateDesc();
    
    // 특정 댓글에 대한 특정 사용자의 신고 여부 확인
    boolean existsByCommentIdAndReportUser(Long commentId, String reportUser);
    
    // 특정 댓글의 신고 개수 조회
    @Query("SELECT COUNT(cr) FROM CommentReport cr WHERE cr.commentId = :commentId")
    long countByCommentId(@Param("commentId") Long commentId);
} 