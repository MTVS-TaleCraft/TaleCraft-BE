package com.talecraft.talecraftbe.novel.episode.dto.response;

import com.talecraft.talecraftbe.novel.episode.model.entity.EpisodeEntity;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ResponseGetEpisodeDto {
    long episodeId;
    long novelId;
    String title;
    String content;
    int view;
    String note;
    LocalDate createDate;


}
