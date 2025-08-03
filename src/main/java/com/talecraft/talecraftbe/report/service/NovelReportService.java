package com.talecraft.talecraftbe.report.service;

import com.talecraft.talecraftbe.report.dto.NovelReportRequest;
import com.talecraft.talecraftbe.report.dto.NovelReportResponse;
import com.talecraft.talecraftbe.report.entity.NovelReport;
import com.talecraft.talecraftbe.report.repository.NovelReportRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NovelReportService {
    
    private static final Logger logger = LoggerFactory.getLogger(NovelReportService.class);
    private final NovelReportRepository novelReportRepository;

    public NovelReportService(NovelReportRepository novelReportRepository) {
        this.novelReportRepository = novelReportRepository;
    }

    /**
     * 작품 신고 생성
     */
    public NovelReportResponse createReport(NovelReportRequest request) {
        // 현재 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String reportUser = authentication.getName();
        logger.info("Creating report for novel {} by user {}", request.novelId(), reportUser);

        // 중복 신고 확인
        if (novelReportRepository.existsByNovelIdAndReportUser(request.novelId(), reportUser)) {
            throw new IllegalArgumentException("이미 신고한 작품입니다.");
        }

        // 신고 생성
        NovelReport report = new NovelReport();
        report.setReportUser(reportUser);
        report.setReportedUser(request.reportedUser());
        report.setReportTag(request.reportTag());
        report.setDescription(request.description());
        report.setReportDate(LocalDateTime.now());
        report.setIsView(false);
        report.setNovelId(request.novelId());

        NovelReport savedReport = novelReportRepository.save(report);
        logger.info("Report created successfully with ID: {}", savedReport.getPostReportId());

        return convertToResponse(savedReport);
    }

    /**
     * 특정 작품의 신고 목록 조회
     */
    public List<NovelReportResponse> getReportsByNovelId(Long novelId) {
        List<NovelReport> reports = novelReportRepository.findByNovelIdOrderByReportDateDesc(novelId);
        return reports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 현재 사용자가 신고한 목록 조회
     */
    public List<NovelReportResponse> getMyReports() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String reportUser = authentication.getName();
        List<NovelReport> reports = novelReportRepository.findByReportUserOrderByReportDateDesc(reportUser);
        return reports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 확인되지 않은 신고 목록 조회 (관리자용)
     */
    public List<NovelReportResponse> getUnviewedReports() {
        List<NovelReport> reports = novelReportRepository.findByIsViewFalseOrderByReportDateDesc();
        return reports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 신고 확인 처리 (관리자용)
     */
    public NovelReportResponse markAsViewed(Long reportId) {
        NovelReport report = novelReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다."));
        
        report.setIsView(true);
        NovelReport savedReport = novelReportRepository.save(report);
        logger.info("Report {} marked as viewed", reportId);
        
        return convertToResponse(savedReport);
    }

    /**
     * 특정 작품의 신고 개수 조회
     */
    public long getReportCountByNovelId(Long novelId) {
        return novelReportRepository.countByNovelId(novelId);
    }
    
    /**
     * 신고된 소설 ID 목록 조회 (관리자용)
     */
    public List<Long> getReportedNovelIds() {
        return novelReportRepository.findDistinctNovelIds();
    }

    /**
     * Entity를 Response DTO로 변환
     */
    private NovelReportResponse convertToResponse(NovelReport report) {
        return new NovelReportResponse(
                report.getPostReportId(),
                report.getReportUser(),
                report.getReportedUser(),
                report.getReportTag(),
                report.getDescription(),
                report.getReportDate(),
                report.getIsView(),
                report.getNovelId()
        );
    }
} 