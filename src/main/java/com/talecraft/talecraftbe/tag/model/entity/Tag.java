package com.talecraft.talecraftbe.tag.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@IdClass(Tag.TagId.class)
public class Tag {
    
    @Id
    @Column(name = "tag_id")
    private Long tagId;
    
    @Id
    @Column(name = "novel_id")
    private Long novelId;
    
    @Column(name = "tag_name", nullable = false)
    private String tagName;
    
    // 복합 기본키를 위한 내부 클래스
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class TagId implements Serializable {
        private Long tagId;
        private Long novelId;
    }
}
