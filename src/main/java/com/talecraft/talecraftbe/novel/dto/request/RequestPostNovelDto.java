package com.talecraft.talecraftbe.novel.dto.request;

import com.talecraft.talecraftbe.novel.model.entity.Availability;
import lombok.Data;

@Data
public class RequestPostNovelDto {
    private String title;
    private String titleImage;
    private String summary;
    private Availability availability;
}
