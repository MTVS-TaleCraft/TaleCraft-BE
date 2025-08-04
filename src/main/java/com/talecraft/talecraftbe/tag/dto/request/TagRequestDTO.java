package com.talecraft.talecraftbe.tag.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class TagRequestDTO {
    private Long novelId;
    private List<String> tagNames;
    
    // 태그 추가 요청
    public static class AddTagsRequest {
        private Long novelId;
        private List<String> tagNames;
    }
    
    // 태그 삭제 요청
    public static class RemoveTagsRequest {
        private Long novelId;
        private List<String> tagNames;
    }
    
    // 태그 검색 요청
    public static class SearchTagsRequest {
        private String tagName;
    }
} 