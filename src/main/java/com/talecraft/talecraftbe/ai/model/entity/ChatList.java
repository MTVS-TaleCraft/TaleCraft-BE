package com.talecraft.talecraftbe.ai.model.entity;

import com.talecraft.talecraftbe.novel.episode.model.entity.EpisodeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chat_list")
@Setter
@Getter
@ToString(exclude = "episode")
public class ChatList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatListId;

    @Column(name = "created_at")
    @Comment("생성 시간")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private EpisodeEntity episode;

    @OneToMany(mappedBy = "chatList", fetch = FetchType.LAZY)
    @Comment("채팅 목록")
    private List<ChatMessage> chatMessageList;
}
