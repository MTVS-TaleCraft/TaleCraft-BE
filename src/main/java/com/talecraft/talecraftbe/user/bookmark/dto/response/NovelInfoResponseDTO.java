package com.talecraft.talecraftbe.user.bookmark.dto.response;


import com.talecraft.talecraftbe.novel.model.entity.NovelEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NovelInfoResponseDTO {
    private Long novelId;
    private String title;
    private String titleImage;
    private String summary;

    public NovelInfoResponseDTO(NovelEntity novel) {
        this.novelId = novel.getNovelId();
        this.title = novel.getTitle();
        this.titleImage = novel.getTitleImage();
        this.summary = novel.getSummary();
    }
}
