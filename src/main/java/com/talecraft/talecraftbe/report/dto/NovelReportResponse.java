package com.talecraft.talecraftbe.report.dto;

import java.time.LocalDateTime;

public record NovelReportResponse(
        Long postReportId,
        String reportUser,
        String reportedUser,
        String reportTag,
        String description,
        LocalDateTime reportDate,
        Boolean isView,
        Long novelId
) {} 