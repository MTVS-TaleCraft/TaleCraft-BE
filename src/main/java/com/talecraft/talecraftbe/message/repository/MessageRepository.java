package com.talecraft.talecraftbe.message.repository;

import com.talecraft.talecraftbe.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // 받은 쪽지 목록 조회 (삭제되지 않은 것만)
    List<Message> findByReceiverAndIsReceiverDeletedFalseOrderByMessageIdDesc(String receiver);
    
    // 보낸 쪽지 목록 조회 (삭제되지 않은 것만)
    List<Message> findBySenderAndIsSenderDeletedFalseOrderByMessageIdDesc(String sender);
    
    // 특정 쪽지 조회 (삭제 여부 확인 포함)
    Optional<Message> findByMessageIdAndReceiverAndIsReceiverDeletedFalse(Long messageId, String receiver);
    
    Optional<Message> findByMessageIdAndSenderAndIsSenderDeletedFalse(Long messageId, String sender);
    
    // 받은 쪽지 개수 조회
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver = :receiver AND m.isReceiverDeleted = false")
    long countReceivedMessages(@Param("receiver") String receiver);
    
    // 보낸 쪽지 개수 조회
    @Query("SELECT COUNT(m) FROM Message m WHERE m.sender = :sender AND m.isSenderDeleted = false")
    long countSentMessages(@Param("sender") String sender);
} 