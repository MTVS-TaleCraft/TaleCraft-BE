package com.talecraft.talecraftbe.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.talecraft.talecraftbe.ai.dto.data.AIOptions;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddAIRequestDTO {
    private boolean useChatList;
    private String question;
    private List<String> beforeQuestionList;
    private List<String> beforeResponseList;
    private AIOptions option;
    private Integer extensionLength;
    private MultipartFile image;
}
