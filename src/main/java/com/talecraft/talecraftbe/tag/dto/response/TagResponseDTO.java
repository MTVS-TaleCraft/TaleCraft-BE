package com.talecraft.talecraftbe.tag.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class TagResponseDTO {
    private Long tagId;
    private String tagName;
    private Long novelId;
    private List<String> tagNames;
    private int tagCount;
    
    // 작품별 태그 목록 응답
    @Getter
    @Setter
    @ToString
    public static class NovelTagsResponse {
        private Long novelId;
        private List<String> tagNames;
        private int tagCount;
    }
    
    // 태그 검색 결과 응답
    @Getter
    @Setter
    @ToString
    public static class TagSearchResponse {
        private List<Long> novelIds;
        private int resultCount;
    }
    
    // 기본 태그 목록 응답
    @Getter
    @Setter
    @ToString
    public static class TagListResponse {
        private List<String> tagNames;
        private int totalCount;
    }
} 