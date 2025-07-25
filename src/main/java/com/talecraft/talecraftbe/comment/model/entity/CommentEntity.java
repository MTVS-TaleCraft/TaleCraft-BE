package com.talecraft.talecraftbe.comment.model.entity;


import com.talecraft.talecraftbe.novel.model.entity.NovelEntity;
import com.talecraft.talecraftbe.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Entity
@Table(name = "comments")
@Getter
@Builder
@AllArgsConstructor
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Comment("댓글ID")
    private long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @Comment("소설 외래키")
    private NovelEntity novel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @Comment("유저 외래키")
    private User user;


    @Column
    @Comment("내용")
    private String content;

    @Column
    @Comment("삭제여부")
    private boolean isDeleted;

    @Column
    @CreatedDate
    @Comment("작성시간")
    private LocalDate createdDate;

    public CommentEntity() {

    }
}
