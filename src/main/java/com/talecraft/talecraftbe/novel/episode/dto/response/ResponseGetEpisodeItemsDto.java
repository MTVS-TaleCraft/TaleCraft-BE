package com.talecraft.talecraftbe.novel.episode.dto.response;

import com.talecraft.talecraftbe.novel.episode.model.entity.EpisodeEntity;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ResponseGetEpisodeItemsDto {
    long episodeId;
    String title;
    int view;
    String note;
    LocalDate createDate;
    public ResponseGetEpisodeItemsDto(EpisodeEntity episodeEntity) {
        this.episodeId = episodeEntity.getEpisodesId();
        this.title = episodeEntity.getTitle();
        this.view = episodeEntity.getView();
        this.note = episodeEntity.getNote();
        this.createDate = episodeEntity.getCreatedDate();
    }
}
