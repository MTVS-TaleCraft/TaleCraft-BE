package com.talecraft.talecraftbe.inquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryRequestDto {
    private String content;
    private String subject;
} 