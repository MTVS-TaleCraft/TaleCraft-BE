package com.talecraft.talecraftbe.ai.controller;

import com.talecraft.talecraftbe.ai.dto.request.AddAIRequestDTO;
import com.talecraft.talecraftbe.ai.dto.request.FindAIRequestDTO;
import com.talecraft.talecraftbe.ai.dto.response.AddAIResponseDTO;
import com.talecraft.talecraftbe.ai.dto.response.FindAIResponseDTO;
import com.talecraft.talecraftbe.ai.exception.AIRequestFailException;
import com.talecraft.talecraftbe.ai.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/ai")
public class AIController {
    private final AIService aiService;

    @Value("${ai.url}")
    private String alURL;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping
    public ResponseEntity<?> getAi(@ModelAttribute FindAIRequestDTO requestDTO) {
        // AI와 대화한 기록 출력
        log.info("GET : /api/ai");
        FindAIResponseDTO chatList = aiService.getChatList(requestDTO);

        return ResponseEntity.ok().body(chatList);
    }

    @PostMapping
    public ResponseEntity<?> postAi(@ModelAttribute AddAIRequestDTO requestDTO) {
        log.info("POST : /api/ai");
        log.info("requestDTO: {}", requestDTO);

        String url = alURL + "/api/chat";
        AddAIResponseDTO responseAIDTO = aiService.requestAI(requestDTO, url);
        if(requestDTO.isUseChatList())
            aiService.addChatMessage(requestDTO, responseAIDTO);

        log.info("responseAIDTO: {}", responseAIDTO);
        return ResponseEntity.ok().body(responseAIDTO);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleAIRequestFailException(AIRequestFailException e) {
        log.error(e.getMessage());

        AddAIResponseDTO responseAIDTO = new AddAIResponseDTO();
        responseAIDTO.setSuccess(false);
        responseAIDTO.setAnswer(e.getMessage());

        return ResponseEntity.internalServerError().body(responseAIDTO);
    }
}
