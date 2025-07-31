package com.talecraft.talecraftbe.novel.episode.dto.request;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class RequestPostEpisodeDto {
    private String title;
    private String content;
    private String note;
    private boolean isNotice;
}
