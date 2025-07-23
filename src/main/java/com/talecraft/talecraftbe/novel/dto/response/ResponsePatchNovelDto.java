package com.talecraft.talecraftbe.novel.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ResponsePatchNovelDto {
    private long id;
    private String message;
    
    public ResponsePatchNovelDto(String message) {
        this.message = message;
    }
}
