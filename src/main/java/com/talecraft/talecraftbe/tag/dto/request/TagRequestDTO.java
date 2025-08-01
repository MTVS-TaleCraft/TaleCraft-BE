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
} 