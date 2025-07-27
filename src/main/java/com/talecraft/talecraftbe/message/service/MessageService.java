package com.talecraft.talecraftbe.message.service;

import com.talecraft.talecraftbe.message.dto.MessageSendRequest;
import com.talecraft.talecraftbe.message.dto.MessageResponse;
import com.talecraft.talecraftbe.message.entity.Message;
import com.talecraft.talecraftbe.message.repository.MessageRepository;
import com.talecraft.talecraftbe.user.entity.User;
import com.talecraft.talecraftbe.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    /**
     * 쪽지 보내기
     */
    public MessageResponse sendMessage(MessageSendRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String senderId = authentication.getName();
        
        // 받는 사람 ID로 사용자 존재 확인
        User receiver = userRepository.findById(request.receiver())
                .orElseThrow(() -> new IllegalArgumentException("받는 사람을 찾을 수 없습니다."));
        
        // 본인에게 쪽지 보내기 방지
        if (senderId.equals(request.receiver())) {
            throw new IllegalArgumentException("본인에게는 쪽지를 보낼 수 없습니다.");
        }
        
        logger.info("Sending message from {} to {}", senderId, request.receiver());

        Message message = new Message();
        message.setSender(senderId);
        message.setReceiver(request.receiver());
        message.setMessageTitle(request.messageTitle());
        message.setDescription(request.description());
        message.setIsSenderDeleted(false);
        message.setIsReceiverDeleted(false);

        Message savedMessage = messageRepository.save(message);
        logger.info("Message sent successfully with ID: {}", savedMessage.getMessageId());

        return convertToResponse(savedMessage);
    }

    /**
     * 받은 쪽지 목록 조회
     */
    public List<MessageResponse> getReceivedMessages() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String receiverId = authentication.getName();
        List<Message> messages = messageRepository.findByReceiverAndIsReceiverDeletedFalseOrderByMessageIdDesc(receiverId);
        
        return messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 보낸 쪽지 목록 조회
     */
    public List<MessageResponse> getSentMessages() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String senderId = authentication.getName();
        List<Message> messages = messageRepository.findBySenderAndIsSenderDeletedFalseOrderByMessageIdDesc(senderId);
        
        return messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 쪽지 상세 조회 (받은 쪽지)
     */
    public MessageResponse getReceivedMessage(Long messageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String receiverId = authentication.getName();
        Message message = messageRepository.findByMessageIdAndReceiverAndIsReceiverDeletedFalse(messageId, receiverId)
                .orElseThrow(() -> new IllegalArgumentException("쪽지를 찾을 수 없습니다."));
        
        return convertToResponse(message);
    }

    /**
     * 쪽지 상세 조회 (보낸 쪽지)
     */
    public MessageResponse getSentMessage(Long messageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String senderId = authentication.getName();
        Message message = messageRepository.findByMessageIdAndSenderAndIsSenderDeletedFalse(messageId, senderId)
                .orElseThrow(() -> new IllegalArgumentException("쪽지를 찾을 수 없습니다."));
        
        return convertToResponse(message);
    }

    /**
     * 받은 쪽지 삭제
     */
    public void deleteReceivedMessage(Long messageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String receiverId = authentication.getName();
        Message message = messageRepository.findByMessageIdAndReceiverAndIsReceiverDeletedFalse(messageId, receiverId)
                .orElseThrow(() -> new IllegalArgumentException("쪽지를 찾을 수 없습니다."));
        
        message.setIsReceiverDeleted(true);
        messageRepository.save(message);
        logger.info("Received message {} deleted by {}", messageId, receiverId);
    }

    /**
     * 보낸 쪽지 삭제
     */
    public void deleteSentMessage(Long messageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String senderId = authentication.getName();
        Message message = messageRepository.findByMessageIdAndSenderAndIsSenderDeletedFalse(messageId, senderId)
                .orElseThrow(() -> new IllegalArgumentException("쪽지를 찾을 수 없습니다."));
        
        message.setIsSenderDeleted(true);
        messageRepository.save(message);
        logger.info("Sent message {} deleted by {}", messageId, senderId);
    }

    /**
     * 받은 쪽지 개수 조회
     */
    public long getReceivedMessageCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String receiverId = authentication.getName();
        return messageRepository.countReceivedMessages(receiverId);
    }

    /**
     * 보낸 쪽지 개수 조회
     */
    public long getSentMessageCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        String senderId = authentication.getName();
        return messageRepository.countSentMessages(senderId);
    }

    /**
     * Entity를 Response DTO로 변환
     */
    private MessageResponse convertToResponse(Message message) {
        return new MessageResponse(
                message.getMessageId(),
                message.getSender(),
                message.getReceiver(),
                message.getMessageTitle(),
                message.getDescription(),
                message.getIsSenderDeleted(),
                message.getIsReceiverDeleted()
        );
    }
} 