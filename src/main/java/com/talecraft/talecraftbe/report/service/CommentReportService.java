package com.talecraft.talecraftbe.report.service;

import com.talecraft.talecraftbe.report.dto.CommentReportRequest;
import com.talecraft.talecraftbe.report.dto.CommentReportResponse;
import com.talecraft.talecraftbe.report.entity.CommentReport;
import com.talecraft.talecraftbe.report.repository.CommentReportRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentReportService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentReportService.class);
    private final CommentReportRepository commentReportRepository;

    public CommentReportService(CommentReportRepository commentReportRepository) {
        this.commentReportRepository = commentReportRepository;
    }

    /**
     * 댓글 신고 생성
     */
    public CommentReportResponse createReport(CommentReportRequest request) {
        // 현재 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String reportUser = authentication.getName();
        logger.info("Creating report for comment {} by user {}", request.commentId(), reportUser);

        // 중복 신고 확인
        if (commentReportRepository.existsByCommentIdAndReportUser(request.commentId(), reportUser)) {
            throw new IllegalArgumentException("이미 신고한 댓글입니다.");
        }

        // 신고 생성
        CommentReport report = new CommentReport();
        report.setReportUser(reportUser);
        report.setReportedUser(request.reportedUser());
        report.setReportTag(request.reportTag());
        report.setDescription(request.description());
        report.setReportDate(LocalDateTime.now());
        report.setIsView(false);
        report.setCommentId(request.commentId());

        CommentReport savedReport = commentReportRepository.save(report);
        logger.info("Report created successfully with ID: {}", savedReport.getCommentReportId());

        return convertToResponse(savedReport);
    }

    /**
     * 특정 댓글의 신고 목록 조회
     */
    public List<CommentReportResponse> getReportsByCommentId(Long commentId) {
        List<CommentReport> reports = commentReportRepository.findByCommentIdOrderByReportDateDesc(commentId);
        return reports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 현재 사용자가 신고한 목록 조회
     */
    public List<CommentReportResponse> getMyReports() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String reportUser = authentication.getName();
        List<CommentReport> reports = commentReportRepository.findByReportUserOrderByReportDateDesc(reportUser);
        return reports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 확인되지 않은 신고 목록 조회 (관리자용)
     */
    public List<CommentReportResponse> getUnviewedReports() {
        List<CommentReport> reports = commentReportRepository.findByIsViewFalseOrderByReportDateDesc();
        return reports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 신고 확인 처리 (관리자용)
     */
    public CommentReportResponse markAsViewed(Long reportId) {
        CommentReport report = commentReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다."));
        
        report.setIsView(true);
        CommentReport savedReport = commentReportRepository.save(report);
        logger.info("Report {} marked as viewed", reportId);
        
        return convertToResponse(savedReport);
    }

    /**
     * 특정 댓글의 신고 개수 조회
     */
    public long getReportCountByCommentId(Long commentId) {
        return commentReportRepository.countByCommentId(commentId);
    }

    /**
     * Entity를 Response DTO로 변환
     */
    private CommentReportResponse convertToResponse(CommentReport report) {
        return new CommentReportResponse(
                report.getCommentReportId(),
                report.getReportUser(),
                report.getReportedUser(),
                report.getReportTag(),
                report.getDescription(),
                report.getReportDate(),
                report.getIsView(),
                report.getCommentId()
        );
    }
} 