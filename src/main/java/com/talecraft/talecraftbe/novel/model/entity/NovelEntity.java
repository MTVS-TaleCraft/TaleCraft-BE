package com.talecraft.talecraftbe.novel.model.entity;


import com.talecraft.talecraftbe.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name ="novels")
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
public class NovelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private long novelId;

    @Column(nullable = false)
    private String title;

    @Column
    private String titleImage;

    @Column
    private String summary;

    @Column(nullable = false)
    private Availability availability;

    public NovelEntity() {

    }

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column
    @ColumnDefault("0")
    boolean isFinished;

    @Column
    @ColumnDefault("0")
    boolean isDeleted;

    @Column
    @ColumnDefault("0")
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
