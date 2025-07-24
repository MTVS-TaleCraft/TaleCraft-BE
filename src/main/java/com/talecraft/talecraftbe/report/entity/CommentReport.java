package com.talecraft.talecraftbe.report.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment_reports")  // 테이블 명
public class CommentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_report_id")  // 신고 ID
    private Long commentReportId;

    @Column(name = "report_user", length = 20, nullable = false)  // 신고자
    private String reportUser;

    @Column(name = "reported_user", length = 20, nullable = false)  // 신고 대상
    private String reportedUser;

    @Column(name = "report_tag", length = 255, nullable = false)  // 신고 태그
    private String reportTag;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)  // 신고 내용
    private String description;

    @Column(name = "report_date", nullable = false)  // 신고 일시
    private LocalDateTime reportDate;

    @Column(name = "is_view", nullable = false)  // 확인 여부
    private Boolean isView;

    // 댓글 엔티티가 이미 있다면 ManyToOne 관계로 매핑
    @Column(name = "comment_id", nullable = false)
    private Long commentId; // 댓글 ID (외래키)
    
    public CommentReport() {
        // JPA 리플렉션용 기본 생성자
    }

    // Getter and Setter methods
    public Long getCommentReportId() {
        return commentReportId;
    }

    public void setCommentReportId(Long commentReportId) {
        this.commentReportId = commentReportId;
    }

    public String getReportUser() {
        return reportUser;
    }

    public void setReportUser(String reportUser) {
        this.reportUser = reportUser;
    }

    public String getReportedUser() {
        return reportedUser;
    }

    public void setReportedUser(String reportedUser) {
        this.reportedUser = reportedUser;
    }

    public String getReportTag() {
        return reportTag;
    }

    public void setReportTag(String reportTag) {
        this.reportTag = reportTag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public Boolean getIsView() {
        return isView;
    }

    public void setIsView(Boolean isView) {
        this.isView = isView;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
}
