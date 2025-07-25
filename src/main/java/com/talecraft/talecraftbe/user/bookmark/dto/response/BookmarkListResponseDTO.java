package com.talecraft.talecraftbe.user.bookmark.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class BookmarkListResponseDTO {
    private boolean status;
    private List<BookmarkResponseDTO> bookmarkList;

    public BookmarkListResponseDTO(List<BookmarkResponseDTO> responseDTOList) {
        this.status = true;
        this.bookmarkList = responseDTOList;
    }
}
