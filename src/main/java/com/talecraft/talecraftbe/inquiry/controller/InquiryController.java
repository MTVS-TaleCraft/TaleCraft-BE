package com.talecraft.talecraftbe.inquiry.controller;

import com.talecraft.talecraftbe.inquiry.dto.InquiryRequestDto;
import com.talecraft.talecraftbe.inquiry.dto.InquiryResponseDto;
import com.talecraft.talecraftbe.inquiry.service.InquiryService;
import com.talecraft.talecraftbe.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inquiry")
public class InquiryController {
    
    private final InquiryService inquiryService;
    
    @Autowired
    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }
    
    @PostMapping("/send")
    public ResponseEntity<InquiryResponseDto> sendInquiry(
            @RequestBody InquiryRequestDto inquiryRequestDto,
            @AuthenticationPrincipal User user) {
        return inquiryService.sendInquiry(inquiryRequestDto, user);
    }
} 