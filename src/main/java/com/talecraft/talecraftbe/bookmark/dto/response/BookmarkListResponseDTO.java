package com.talecraft.talecraftbe.bookmark.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class BookmarkListResponseDTO {
    private List<BookmarkDTO> bookmarks;
    private int totalCount;
    
    @Getter
    @Setter
    @ToString
    public static class BookmarkDTO {
        private Long bookmarkId;
        private Long novelId;
        private String novelTitle;
    }
}
