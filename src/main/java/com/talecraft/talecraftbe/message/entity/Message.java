package com.talecraft.talecraftbe.message.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "sender", length = 20, nullable = false)
    private String sender;

    @Column(name = "receiver", length = 20, nullable = false)
    private String receiver;

    @Column(name = "message_title", length = 255, nullable = false)
    private String messageTitle;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "is_sender_deleted", nullable = false)
    private Boolean isSenderDeleted = false;

    @Column(name = "is_receiver_deleted", nullable = false)
    private Boolean isReceiverDeleted = false;

    public Message() {}

    // Getter/Setter
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public String getMessageTitle() { return messageTitle; }
    public void setMessageTitle(String messageTitle) { this.messageTitle = messageTitle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsSenderDeleted() { return isSenderDeleted; }
    public void setIsSenderDeleted(Boolean isSenderDeleted) { this.isSenderDeleted = isSenderDeleted; }

    public Boolean getIsReceiverDeleted() { return isReceiverDeleted; }
    public void setIsReceiverDeleted(Boolean isReceiverDeleted) { this.isReceiverDeleted = isReceiverDeleted; }
} 