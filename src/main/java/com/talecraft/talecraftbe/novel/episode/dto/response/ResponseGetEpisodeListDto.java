package com.talecraft.talecraftbe.novel.episode.dto.response;


import lombok.Data;

import java.util.List;

@Data
public class ResponseGetEpisodeListDto {
    List<ResponseGetEpisodeItemsDto> episodesList;
}
