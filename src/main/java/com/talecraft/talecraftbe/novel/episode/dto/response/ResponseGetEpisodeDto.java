package com.talecraft.talecraftbe.novel.episode.dto.response;

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
