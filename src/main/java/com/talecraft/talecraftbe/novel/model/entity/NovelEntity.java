package com.talecraft.talecraftbe.novel.model.entity;


import com.talecraft.talecraftbe.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

@Entity
@Table(name ="novels")
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
public class NovelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    @Comment("소설 ID")
    private long novelId;

    @Column(nullable = false)
    @Comment("소설 제목")
    private String title;

    @Column
    @Comment("소설 이미지")
    private String titleImage;

    @Column
    @Comment("소설 개요")
    private String summary;

    @Column(nullable = false)
    @Comment("소설 공개 설정")
    private Availability availability;

    public NovelEntity() {
    }

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column
    @ColumnDefault("0")
    @Comment("소설 연재 여부")
    boolean isFinished;

    @Column
    @ColumnDefault("0")
    @Comment("소설 삭제 여부")
    boolean isDeleted;

    @Column
    @ColumnDefault("0")
    @Comment("소설 금지 여부")
    boolean isBanned;

    public void updateIsBanned(boolean isBanned) {
        this.isBanned = isBanned;
    }

    public void updateTitle(String title) {
        this.title = title;
    }
    public void updateSummary(String summary) {
        this.summary = summary;
    }
    public void updateAvailability(Availability availability) {
        this.availability = availability;
    }
    public void updateTitleImage(String titleImage) {
        this.titleImage = titleImage;
    }
}
