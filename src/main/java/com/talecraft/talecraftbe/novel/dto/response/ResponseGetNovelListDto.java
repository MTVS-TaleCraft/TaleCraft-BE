package com.talecraft.talecraftbe.novel.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ResponseGetNovelListDto {
    private List<ResponseGetNovelDto> novelList;
    private int page;
    private int pageSize;
    private int totalElements;
    private int totalPages;
}
