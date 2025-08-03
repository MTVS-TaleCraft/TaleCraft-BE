package com.talecraft.talecraftbe.report.controller;

import com.talecraft.talecraftbe.report.service.NovelReportService;
import com.talecraft.talecraftbe.report.dto.NovelReportRequest;
import com.talecraft.talecraftbe.report.dto.NovelReportResponse;
import com.talecraft.talecraftbe.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class NovelReportController {

    private static final Logger logger = LoggerFactory.getLogger(NovelReportController.class);
    private final NovelReportService novelReportService;

    public NovelReportController(NovelReportService novelReportService) {
        this.novelReportService = novelReportService;
    }

    /**
     * 작품 신고 생성
     */
    @PostMapping("/novels")
    public ResponseEntity<?> createNovelReport(@RequestBody NovelReportRequest request) {
        try {
            NovelReportResponse response = novelReportService.createReport(request);
            logger.info("Novel report created successfully for novel ID: {}", request.novelId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to create novel report: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating novel report", e);
            return ResponseEntity.badRequest().body(Map.of("error", "신고 생성에 실패했습니다."));
        }
    }

    /**
     * 특정 작품의 신고 목록 조회
     */
    @GetMapping("/novels/{novelId}")
    public ResponseEntity<?> getReportsByNovelId(@PathVariable Long novelId) {
        try {
            List<NovelReportResponse> reports = novelReportService.getReportsByNovelId(novelId);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            logger.error("Error retrieving reports for novel ID: {}", novelId, e);
            return ResponseEntity.badRequest().body(Map.of("error", "신고 목록 조회에 실패했습니다."));
        }
    }

    /**
     * 내가 신고한 목록 조회
     */
    @GetMapping("/my-novel-reports")
    public ResponseEntity<?> getMyNovelReports() {
        try {
            List<NovelReportResponse> reports = novelReportService.getMyReports();
            return ResponseEntity.ok(reports);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to get my novel reports: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving my novel reports", e);
            return ResponseEntity.badRequest().body(Map.of("error", "신고 목록 조회에 실패했습니다."));
        }
    }

    /**
     * 확인되지 않은 신고 목록 조회 (관리자용)
     */
    @GetMapping("/unviewed-novels")
    public ResponseEntity<?> getUnviewedNovelReports() {
        try {
            List<NovelReportResponse> reports = novelReportService.getUnviewedReports();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            logger.error("Error retrieving unviewed novel reports", e);
            return ResponseEntity.badRequest().body(Map.of("error", "미확인 신고 목록 조회에 실패했습니다."));
        }
    }

    /**
     * 신고 확인 처리 (관리자용)
     */
    @PatchMapping("/novels/{reportId}/view")
    public ResponseEntity<?> markNovelReportAsViewed(@PathVariable Long reportId) {
        try {
            NovelReportResponse response = novelReportService.markAsViewed(reportId);
            logger.info("Novel report {} marked as viewed", reportId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to mark novel report as viewed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error marking novel report as viewed", e);
            return ResponseEntity.badRequest().body(Map.of("error", "신고 확인 처리에 실패했습니다."));
        }
    }

    /**
     * 특정 작품의 신고 개수 조회
     */
    @GetMapping("/novels/{novelId}/count")
    public ResponseEntity<?> getReportCountByNovelId(@PathVariable Long novelId) {
        try {
            long count = novelReportService.getReportCountByNovelId(novelId);
            return ResponseEntity.ok(Map.of("reportCount", count));
        } catch (Exception e) {
            logger.error("Error retrieving report count for novel ID: {}", novelId, e);
            return ResponseEntity.badRequest().body(Map.of("error", "신고 개수 조회에 실패했습니다."));
        }
    }
    
    /**
     * 신고된 소설 ID 목록 조회 (관리자용)
     */
    @GetMapping("/reported-novels")
    public ResponseEntity<?> getReportedNovelIds(@AuthenticationPrincipal User user) {
        try {
            logger.info("getReportedNovelIds called by user: {}", user != null ? user.getId() : "null");
            
            // 관리자 권한 확인
            if (user == null) {
                logger.warn("Unauthorized access attempt to reported novels");
                return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
            }
            
            logger.info("User authority ID: {}", user.getAuthorityId());
            
            if (user.getAuthorityId() == null || user.getAuthorityId() != 3L) {
                logger.warn("Non-admin user {} attempted to access reported novels", user.getId());
                return ResponseEntity.status(403).body(Map.of("error", "관리자 권한이 필요합니다."));
            }
            
            List<Long> reportedNovelIds = novelReportService.getReportedNovelIds();
            logger.info("Admin {} retrieved {} reported novel IDs", user.getId(), reportedNovelIds.size());
            return ResponseEntity.ok(Map.of("reportedNovelIds", reportedNovelIds));
        } catch (Exception e) {
            logger.error("Error retrieving reported novel IDs", e);
            return ResponseEntity.badRequest().body(Map.of("error", "신고된 소설 목록 조회에 실패했습니다."));
        }
    }
} 