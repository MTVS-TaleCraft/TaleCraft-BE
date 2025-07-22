package com.talecraft.talecraftbe.novel.dto.request;

import com.talecraft.talecraftbe.novel.model.entity.Availability;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestPostNovelDto {
    private String title;
    private String titleImage;
    private String summary;
    private Availability availability;
}
