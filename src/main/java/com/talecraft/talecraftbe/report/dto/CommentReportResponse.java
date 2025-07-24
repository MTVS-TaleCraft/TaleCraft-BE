package com.talecraft.talecraftbe.report.dto;

import java.time.LocalDateTime;

public record CommentReportResponse(
        Long commentReportId,
        String reportUser,
        String reportedUser,
        String reportTag,
        String description,
        LocalDateTime reportDate,
        Boolean isView,
        Long commentId
) {} 