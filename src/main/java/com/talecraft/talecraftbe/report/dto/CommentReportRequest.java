package com.talecraft.talecraftbe.report.dto;

public record CommentReportRequest(
        Long commentId,
        String reportedUser,
        String reportTag,
        String description
) {} 