package com.talecraft.talecraftbe.report.dto;

public record NovelReportRequest(
        Long novelId,
        String reportedUser,
        String reportTag,
        String description
) {} 