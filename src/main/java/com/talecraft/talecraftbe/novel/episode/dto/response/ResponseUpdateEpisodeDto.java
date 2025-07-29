package com.talecraft.talecraftbe.novel.episode.dto.response;

import lombok.Data;

@Data
public class ResponseUpdateEpisodeDto {
    private long episodeId;
    private String message;
    public ResponseUpdateEpisodeDto(Long episodeId, String message) {
        this.episodeId = episodeId;
        this.message = message;
    }
}
