package com.talecraft.talecraftbe.report.controller;

import com.talecraft.talecraftbe.report.service.CommentReportService;
import com.talecraft.talecraftbe.report.dto.CommentReportRequest;
import com.talecraft.talecraftbe.report.dto.CommentReportResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class CommentReportController {

    private static final Logger logger = LoggerFactory.getLogger(CommentReportController.class);
    private final CommentReportService commentReportService;

    public CommentReportController(CommentReportService commentReportService) {
        this.commentReportService = commentReportService;
    }

    /**
     * 댓글 신고 생성
     */
    @PostMapping("/comments")
    public ResponseEntity<?> createCommentReport(@RequestBody CommentReportRequest request) {
        try {
            CommentReportResponse response = commentReportService.createReport(request);
            logger.info("Comment report created successfully for comment ID: {}", request.commentId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to create comment report: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating comment report", e);
            return ResponseEntity.badRequest().body(Map.of("error", "신고 생성에 실패했습니다."));
        }
    }

    /**
     * 특정 댓글의 신고 목록 조회
     */
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<?> getReportsByCommentId(@PathVariable Long commentId) {
        try {
            List<CommentReportResponse> reports = commentReportService.getReportsByCommentId(commentId);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            logger.error("Error retrieving reports for comment ID: {}", commentId, e);
            return ResponseEntity.badRequest().body(Map.of("error", "신고 목록 조회에 실패했습니다."));
        }
    }

    /**
     * 내가 신고한 목록 조회
     */
    @GetMapping("/my-reports")
    public ResponseEntity<?> getMyReports() {
        try {
            List<CommentReportResponse> reports = commentReportService.getMyReports();
            return ResponseEntity.ok(reports);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to get my reports: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving my reports", e);
            return ResponseEntity.badRequest().body(Map.of("error", "신고 목록 조회에 실패했습니다."));
        }
    }

    /**
     * 확인되지 않은 신고 목록 조회 (관리자용)
     */
    @GetMapping("/unviewed")
    public ResponseEntity<?> getUnviewedReports() {
        try {
            List<CommentReportResponse> reports = commentReportService.getUnviewedReports();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            logger.error("Error retrieving unviewed reports", e);
            return ResponseEntity.badRequest().body(Map.of("error", "미확인 신고 목록 조회에 실패했습니다."));
        }
    }

    /**
     * 신고 확인 처리 (관리자용)
     */
    @PatchMapping("/{reportId}/view")
    public ResponseEntity<?> markReportAsViewed(@PathVariable Long reportId) {
        try {
            CommentReportResponse response = commentReportService.markAsViewed(reportId);
            logger.info("Report {} marked as viewed", reportId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to mark report as viewed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error marking report as viewed", e);
            return ResponseEntity.badRequest().body(Map.of("error", "신고 확인 처리에 실패했습니다."));
        }
    }

    /**
     * 특정 댓글의 신고 개수 조회
     */
    @GetMapping("/comments/{commentId}/count")
    public ResponseEntity<?> getReportCountByCommentId(@PathVariable Long commentId) {
        try {
            long count = commentReportService.getReportCountByCommentId(commentId);
            return ResponseEntity.ok(Map.of("reportCount", count));
        } catch (Exception e) {
            logger.error("Error retrieving report count for comment ID: {}", commentId, e);
            return ResponseEntity.badRequest().body(Map.of("error", "신고 개수 조회에 실패했습니다."));
        }
    }
} 