package com.talecraft.talecraftbe.novel.chapter.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Entity
@Table(name = "novels_chapters")
public class ChapterEntity {
    @Id
    public Long novelChapterId;

    @Column
    public String title;

    @Column
    public String content;

    @Column
    public String note;

    @CreatedDate
    public Date createdAt;

    public ChapterEntity(String content, String note, String title) {
        this.content = content;
        this.note = note;
        this.title = title;
    }
}
