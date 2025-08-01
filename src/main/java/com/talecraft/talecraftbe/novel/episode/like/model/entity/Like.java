package com.talecraft.talecraftbe.novel.episode.like.model.entity;

import com.talecraft.talecraftbe.novel.episode.model.entity.EpisodeEntity;
import com.talecraft.talecraftbe.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "likes",
        // 중복 좋아요 방지
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "episode_episodes_id"})
)
@Getter
@Setter
@ToString(exclude = {"episode", "user"})
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("좋아요 한 회차")
    private EpisodeEntity episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("좋아요 한 유저")
    private User user;
}
