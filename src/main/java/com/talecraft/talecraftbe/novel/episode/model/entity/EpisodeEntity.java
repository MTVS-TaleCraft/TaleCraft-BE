package com.talecraft.talecraftbe.novel.episode.model.entity;


import com.talecraft.talecraftbe.novel.episode.dto.request.RequestUpdateEpisodeDto;
import com.talecraft.talecraftbe.novel.model.entity.NovelEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

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


    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private NovelEntity novel;

    @Column(length = 100)
    @Comment("소설 화수 제목")
    private String title;

    @Column(length = 5000)
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
    private Boolean isNotice;
    
    @Column
    @Comment("소설 화수 삭제 여부")
    private Boolean isDeleted;

    public EpisodeEntity() {
    }

    public EpisodeEntity(String title, String content, String note) {
        this.title = title;
        this.content = content;
        this.note = note;
        this.isNotice = false;
        this.isDeleted = false;
    }

    public void updateDeleted() {
        this.isDeleted = true;
    }

    public void updateEpisode(RequestUpdateEpisodeDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.note = dto.getNote();
        this.isDeleted = dto.isDeleted();
        this.isNotice = dto.isNotice();
    }
}
