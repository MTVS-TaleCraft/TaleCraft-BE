package com.talecraft.talecraftbe.ai.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "chat_massages")
@Setter
@Getter
@ToString(exclude = "chatList")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatMessageId;

    @Column(name = "question_message")
    @Comment("유저의 질문")
    private String questionMessage;

    @Column(name = "response_message")
    @Comment("AI의 대답")
    private String responseMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("채팅이 소속된 챗 리스트")
    private ChatList chatList;
}
