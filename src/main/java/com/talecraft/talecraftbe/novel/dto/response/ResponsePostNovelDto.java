package com.talecraft.talecraftbe.novel.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePostNovelDto {
    long novelId;
    String message;
}
