package com.talecraft.talecraftbe.novel.dto.request;

import com.talecraft.talecraftbe.novel.model.entity.Availability;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RequestPatchNovelDto {
    private String title;
    private String titleImage;
    private String summary;
    private Availability availability;
    private boolean isFinished;
    private boolean isDeleted;
    private boolean isBanned;
}
