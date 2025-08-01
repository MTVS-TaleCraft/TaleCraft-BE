package com.talecraft.talecraftbe.tag.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class TagResponseDTO {
    private Long novelId;
    private List<String> tagNames;
    private int tagCount;
} 