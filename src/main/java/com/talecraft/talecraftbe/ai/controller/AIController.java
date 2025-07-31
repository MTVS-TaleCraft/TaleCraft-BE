package com.talecraft.talecraftbe.ai.controller;

import com.talecraft.talecraftbe.ai.dto.request.AddAIRequestDTO;
import com.talecraft.talecraftbe.ai.dto.response.AddAIResponseDTO;
import com.talecraft.talecraftbe.ai.dto.response.FindAIResponseDTO;
import com.talecraft.talecraftbe.ai.exception.AIRequestFailException;
import com.talecraft.talecraftbe.ai.service.AIService;
import com.talecraft.talecraftbe.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/api/ai")
public class AIController {
    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/{episodeId}")
    public ResponseEntity<?> getAi(@AuthenticationPrincipal User user,
                                   @PathVariable Long episodeId,
                                   @RequestParam Long chatListId) {
        // AI와 대화한 기록 출력
        log.info("GET : /api/ai/{}" , episodeId);
        aiService.checkAccess(user, episodeId);
        FindAIResponseDTO chatList = aiService.getChatList(user, episodeId, chatListId);

        return ResponseEntity.ok().body(chatList);
    }

    @Operation(summary = "AI 사용 API", description = "AI를 사용하기 위한 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "AI 응답 성공",
                    content = @Content(schema = @Schema(implementation = AddAIResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "AI 응답 실패[에피소드ID나 챗ID를 찾지 못함]",
            content = @Content(schema = @Schema(implementation = AddAIResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "AI 통신 실패",
            content = @Content(schema = @Schema(implementation = AddAIResponseDTO.class))),
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postAi(@AuthenticationPrincipal User user,
                                    @ModelAttribute AddAIRequestDTO requestDTO) {
        log.info("POST : /api/ai");
        log.info("requestDTO: {}", requestDTO);

        aiService.checkAccess(user, requestDTO.getEpisodeId());

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

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException e) {
        log.error(e.getMessage());

        AddAIResponseDTO responseAIDTO = new AddAIResponseDTO();
        responseAIDTO.setStatus(false);
        responseAIDTO.setResponse(e.getMessage());

        return ResponseEntity.badRequest().body(responseAIDTO);
    }
    @ExceptionHandler(AIRequestFailException.class)
    public ResponseEntity<?> handleAIRequestFailException(AIRequestFailException e) {
        log.error(e.getMessage());

        AddAIResponseDTO responseAIDTO = new AddAIResponseDTO();
        responseAIDTO.setStatus(false);
        responseAIDTO.setResponse(e.getMessage());

        return ResponseEntity.internalServerError().body(responseAIDTO);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        log.error(e.getMessage());

        AddAIResponseDTO responseAIDTO = new AddAIResponseDTO();
        responseAIDTO.setStatus(false);
        responseAIDTO.setResponse(e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseAIDTO);
    }
}
