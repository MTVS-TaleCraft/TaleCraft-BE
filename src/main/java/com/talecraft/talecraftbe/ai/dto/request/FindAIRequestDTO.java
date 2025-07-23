package com.talecraft.talecraftbe.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class FindAIRequestDTO {
    private Long novelChapterId;
    private Long chatListId;
}
