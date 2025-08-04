package com.talecraft.talecraftbe.novel.dto.response;


import com.talecraft.talecraftbe.novel.model.entity.Availability;
import com.talecraft.talecraftbe.novel.model.entity.NovelEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ResponseGetNovelDto {
    private long novelId;
    private String author;
    private String title;
    private String titleImage;
    private String summary;
    private Availability availability;
    private List<String> tags;
    boolean isFinished;
    boolean isDeleted;
    boolean isBanned;
}
