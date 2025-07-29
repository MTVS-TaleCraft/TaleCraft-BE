package com.talecraft.talecraftbe.novel.episode.dto.response;


import lombok.Data;

@Data
public class ResponseDeleteEpisodeDto {
    String message;

    public ResponseDeleteEpisodeDto(String message) {
        this.message = message;
    }
}
