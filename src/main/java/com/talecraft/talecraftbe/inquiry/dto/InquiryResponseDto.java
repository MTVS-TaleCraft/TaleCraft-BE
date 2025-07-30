package com.talecraft.talecraftbe.inquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponseDto {
    private String message;
    private boolean success;
} 