package com.talecraft.talecraftbe.message.controller;

import com.talecraft.talecraftbe.message.dto.MessageSendRequest;
import com.talecraft.talecraftbe.message.dto.MessageResponse;
import com.talecraft.talecraftbe.message.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 쪽지 보내기
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody @Valid MessageSendRequest request) {
        try {
            MessageResponse response = messageService.sendMessage(request);
            logger.info("Message sent successfully from {} to {}", response.sender(), response.receiver());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to send message: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error sending message", e);
            return ResponseEntity.badRequest().body(Map.of("error", "쪽지 전송에 실패했습니다."));
        }
    }

    /**
     * 받은 쪽지 목록 조회
     */
    @GetMapping("/received")
    public ResponseEntity<?> getReceivedMessages() {
        try {
            List<MessageResponse> messages = messageService.getReceivedMessages();
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to get received messages: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving received messages", e);
            return ResponseEntity.badRequest().body(Map.of("error", "받은 쪽지 목록 조회에 실패했습니다."));
        }
    }

    /**
     * 보낸 쪽지 목록 조회
     */
    @GetMapping("/sent")
    public ResponseEntity<?> getSentMessages() {
        try {
            List<MessageResponse> messages = messageService.getSentMessages();
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to get sent messages: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving sent messages", e);
            return ResponseEntity.badRequest().body(Map.of("error", "보낸 쪽지 목록 조회에 실패했습니다."));
        }
    }

    /**
     * 받은 쪽지 상세 조회
     */
    @GetMapping("/received/{messageId}")
    public ResponseEntity<?> getReceivedMessage(@PathVariable Long messageId) {
        try {
            MessageResponse message = messageService.getReceivedMessage(messageId);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to get received message: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving received message", e);
            return ResponseEntity.badRequest().body(Map.of("error", "받은 쪽지 조회에 실패했습니다."));
        }
    }

    /**
     * 보낸 쪽지 상세 조회
     */
    @GetMapping("/sent/{messageId}")
    public ResponseEntity<?> getSentMessage(@PathVariable Long messageId) {
        try {
            MessageResponse message = messageService.getSentMessage(messageId);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to get sent message: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving sent message", e);
            return ResponseEntity.badRequest().body(Map.of("error", "보낸 쪽지 조회에 실패했습니다."));
        }
    }

    /**
     * 받은 쪽지 삭제
     */
    @DeleteMapping("/received/{messageId}")
    public ResponseEntity<?> deleteReceivedMessage(@PathVariable Long messageId) {
        try {
            messageService.deleteReceivedMessage(messageId);
            logger.info("Received message {} deleted successfully", messageId);
            return ResponseEntity.ok(Map.of("message", "받은 쪽지가 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to delete received message: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting received message", e);
            return ResponseEntity.badRequest().body(Map.of("error", "받은 쪽지 삭제에 실패했습니다."));
        }
    }

    /**
     * 보낸 쪽지 삭제
     */
    @DeleteMapping("/sent/{messageId}")
    public ResponseEntity<?> deleteSentMessage(@PathVariable Long messageId) {
        try {
            messageService.deleteSentMessage(messageId);
            logger.info("Sent message {} deleted successfully", messageId);
            return ResponseEntity.ok(Map.of("message", "보낸 쪽지가 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to delete sent message: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting sent message", e);
            return ResponseEntity.badRequest().body(Map.of("error", "보낸 쪽지 삭제에 실패했습니다."));
        }
    }

    /**
     * 받은 쪽지 개수 조회
     */
    @GetMapping("/received/count")
    public ResponseEntity<?> getReceivedMessageCount() {
        try {
            long count = messageService.getReceivedMessageCount();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to get received message count: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving received message count", e);
            return ResponseEntity.badRequest().body(Map.of("error", "받은 쪽지 개수 조회에 실패했습니다."));
        }
    }

    /**
     * 보낸 쪽지 개수 조회
     */
    @GetMapping("/sent/count")
    public ResponseEntity<?> getSentMessageCount() {
        try {
            long count = messageService.getSentMessageCount();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to get sent message count: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving sent message count", e);
            return ResponseEntity.badRequest().body(Map.of("error", "보낸 쪽지 개수 조회에 실패했습니다."));
        }
    }
} 