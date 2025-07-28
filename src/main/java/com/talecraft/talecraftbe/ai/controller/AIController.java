package com.talecraft.talecraftbe.ai.controller;

import com.talecraft.talecraftbe.ai.dto.request.AddAIRequestDTO;
import com.talecraft.talecraftbe.ai.dto.response.AddAIResponseDTO;
import com.talecraft.talecraftbe.ai.dto.response.FindAIResponseDTO;
import com.talecraft.talecraftbe.ai.exception.AIRequestFailException;
import com.talecraft.talecraftbe.ai.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/ai")
public class AIController {
    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/{episodeId}")
    public ResponseEntity<?> getAi(@PathVariable Long episodeId, @RequestParam Long chatListId) {
        // AI와 대화한 기록 출력
        log.info("GET : /api/ai/{}" , episodeId);
        if(episodeId == null && chatListId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", false);
            response.put("message", "에피소드ID 또는 챗리스트ID 중 하나는 필수로 주어야 합니다!");
            return ResponseEntity.badRequest().body(response);
        }
        FindAIResponseDTO chatList = aiService.getChatList(episodeId, chatListId);

        return ResponseEntity.ok().body(chatList);
    }

    @PostMapping
    public ResponseEntity<?> postAi(@ModelAttribute AddAIRequestDTO requestDTO) {
        log.info("POST : /api/ai");
        log.info("requestDTO: {}", requestDTO);

        if(requestDTO.isUseChatList() && requestDTO.getEpisodeId() != null) {
            requestDTO.setChatListId(aiService.addChatList(requestDTO));
        }
        AddAIResponseDTO responseAIDTO = aiService.requestAI(requestDTO);
        if(requestDTO.getChatListId() != null) {
            aiService.addChatMessage(requestDTO, responseAIDTO);
        }

        log.info("responseAIDTO: {}", responseAIDTO);
        return ResponseEntity.ok().body(responseAIDTO);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleAIRequestFailException(AIRequestFailException e) {
        log.error(e.getMessage());

        AddAIResponseDTO responseAIDTO = new AddAIResponseDTO();
        responseAIDTO.setStatus(false);
        responseAIDTO.setResponse(e.getMessage());

        return ResponseEntity.internalServerError().body(responseAIDTO);
    }
}
