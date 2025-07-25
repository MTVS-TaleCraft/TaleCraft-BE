package com.talecraft.talecraftbe.novel.episode.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;

import javax.swing.text.StyledEditorKit;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "episodes")
@Builder
@Getter
@AllArgsConstructor
public class EpisodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Comment("소설 화수 ID")
    private Long episodesId;

    @ManyToOne
    private EpisodeEntity episode;

    @Column
    @Comment("소설 화수 제목")
    private String title;

    @Column
    @Comment("소설 화수 내용")
    private String content;

    @Column
    @Comment("조회수")
    private Integer view;

    @Column
    @Comment("공지")
    private String note;

    @CreatedDate
    @Comment("생성일")
    private LocalDate createdDate;
    
    @Column
    @Comment("공지 여부")
    private Boolean is_notice;
    
    @Column
    @Comment("소설 화수 삭제 여부")
    private Boolean is_deleted;

    public EpisodeEntity() {
    }

    public EpisodeEntity(String title, String content, String note) {
        this.title = title;
        this.content = content;
        this.note = note;
        this.is_notice = false;
        this.is_deleted = false;
    }

    public void updateDeleted() {
        this.is_deleted = true;
    }
}
