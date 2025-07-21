package com.talecraft.talecraftbe.novel.episode.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestPostEpisodeDto {
    String title;
    String content;
    String view;
    String note;
    boolean isNoitce;
    boolean isDeleated;
}
