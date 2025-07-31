package com.talecraft.talecraftbe.user.bookmark.dto.response;

import com.talecraft.talecraftbe.user.bookmark.model.entity.Bookmark;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BookmarkResponseDTO {
    private Long bookmarkId;
    private NovelInfoResponseDTO novelInfo;

    public BookmarkResponseDTO(Bookmark bookmark) {
        this.bookmarkId = bookmark.getBookmarkId();
        this.novelInfo = new NovelInfoResponseDTO(bookmark.getNovel());
    }
}
