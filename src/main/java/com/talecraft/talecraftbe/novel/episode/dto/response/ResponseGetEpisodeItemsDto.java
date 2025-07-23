package com.talecraft.talecraftbe.novel.episode.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ResponseGetEpisodeItemsDto {
    long episodeId;
    long novelId;
    String title;
    int view;
    String note;
    LocalDate createDate;
}
