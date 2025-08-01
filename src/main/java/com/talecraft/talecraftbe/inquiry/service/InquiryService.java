package com.talecraft.talecraftbe.inquiry.service;

import com.talecraft.talecraftbe.inquiry.dto.InquiryRequestDto;
import com.talecraft.talecraftbe.inquiry.dto.InquiryResponseDto;
import com.talecraft.talecraftbe.inquiry.entity.Inquiry;
import com.talecraft.talecraftbe.inquiry.repository.InquiryRepository;
import com.talecraft.talecraftbe.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InquiryService {
    
    private final InquiryRepository inquiryRepository;
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String adminEmail;
    
    @Autowired
    public InquiryService(InquiryRepository inquiryRepository, JavaMailSender mailSender) {
        this.inquiryRepository = inquiryRepository;
        this.mailSender = mailSender;
    }
    
    public ResponseEntity<InquiryResponseDto> sendInquiry(InquiryRequestDto inquiryRequestDto, User user) {
        try {
            // 문의 내용을 데이터베이스에 저장
            Inquiry inquiry = new Inquiry();
            inquiry.setUser(user);
            inquiry.setSubject(inquiryRequestDto.getSubject());
            inquiry.setContent(inquiryRequestDto.getContent());
            
            inquiryRepository.save(inquiry);
            
            // 관리자 이메일로 문의 내용 전송
            sendInquiryEmail(inquiryRequestDto, user);
            
            return ResponseEntity.ok(new InquiryResponseDto("문의가 성공적으로 전송되었습니다.", true));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new InquiryResponseDto("문의 전송에 실패했습니다: " + e.getMessage(), false));
        }
    }
    
    public ResponseEntity<Map<String, Object>> getAllInquiries() {
        try {
            List<Inquiry> inquiries = inquiryRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("inquiries", inquiries);
            response.put("totalCount", inquiries.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "문의 목록을 가져오는데 실패했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    private void sendInquiryEmail(InquiryRequestDto inquiryRequestDto, User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminEmail);
        message.setSubject("[TaleCraft 문의] " + inquiryRequestDto.getSubject());
        
        String emailContent = String.format(
                "새로운 문의가 접수되었습니다.\n\n" +
                "사용자 정보:\n" +
                "- 사용자 ID: %s\n" +
                "- 사용자 이메일: %s\n\n" +
                "문의 제목: %s\n\n" +
                "문의 내용:\n%s",
                user.getId(),
                user.getEmail(),
                inquiryRequestDto.getSubject(),
                inquiryRequestDto.getContent()
        );
        
        message.setText(emailContent);
        mailSender.send(message);
    }
} 