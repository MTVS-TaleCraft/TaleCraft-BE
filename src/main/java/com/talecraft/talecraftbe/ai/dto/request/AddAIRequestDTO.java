package com.talecraft.talecraftbe.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddAIRequestDTO {
    private boolean useChatList;
    private String question;
    private String option;
    private MultipartFile image;
}
