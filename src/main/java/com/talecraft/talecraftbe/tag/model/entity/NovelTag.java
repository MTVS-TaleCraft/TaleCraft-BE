package com.talecraft.talecraftbe.tag.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Entity
@Table(name = "novel_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@IdClass(NovelTag.NovelTagId.class)
public class NovelTag {
    
    @Id
    @Column(name = "novel_id")
    private Long novelId;
    
    @Id
    @Column(name = "tag_id")
    private Long tagId;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new java.util.Date();
    }
    
    // 복합 기본키를 위한 내부 클래스
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class NovelTagId implements Serializable {
        private Long novelId;
        private Long tagId;
    }
} 