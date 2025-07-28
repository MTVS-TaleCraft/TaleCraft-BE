package com.talecraft.talecraftbe.novel.episode.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestUpdateEpisodeDto {
    private String title;
    private String content;
    private String note;
    private boolean isNotice;
    private boolean isDeleted;

}
