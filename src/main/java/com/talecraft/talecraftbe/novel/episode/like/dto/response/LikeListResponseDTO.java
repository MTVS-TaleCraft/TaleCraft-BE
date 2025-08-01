package com.talecraft.talecraftbe.novel.episode.like.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class LikeListResponseDTO {
    private boolean status;
    private String message;
    private List<Long> likes;

    public LikeListResponseDTO(List<Long> likes, String message) {
        this.status = true;
        this.message = message;
        this.likes = likes;
    }
}
