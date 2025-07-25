package com.talecraft.talecraftbe.novel.episode.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private Long episodesId;

    @ManyToOne
    private EpisodeEntity episode;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private Integer view;

    @Column
    private String note;

    @CreatedDate
    private LocalDate createdDate;
    
    @Column
    private Boolean is_notice;
    
    @Column
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
}
