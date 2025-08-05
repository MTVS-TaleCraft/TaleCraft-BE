package com.talecraft.talecraftbe.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.talecraft.talecraftbe.ai.dto.data.AIOptions;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddAIRequestDTO {
    @Schema(description = "소설 아이디", example = "1")
    private Long novelId;
    @Schema(description = "회차 아이디", example = "1")
    private Long episodeId;
    @Schema(description = "챗목록 아이디", example = "1")
    private Long chatListId;
    @Schema(description = "QA형식 사용여부", example = "false")
    private boolean useChatList;
    @Schema(description = "질문", example = "여기에 질문 입력")
    private String question;
    @Schema(description = "이전 질문 목록")
    private List<String> beforeQuestionList;
    @Schema(description = "이전 응답 목록")
    private List<String> beforeResponseList;
    @Schema(description = "AI 옵션", example = "NORMAL", allowableValues = {"NORMAL", "SPELL_CHECK", "STORY_EXTENSION", "MAKE_NAME"})
    private AIOptions option;
    @Schema(description = "이야기 길이 늘리기 최소 값", example = "6000", maximum = "15000")
    private Integer extensionLength;
    @Schema(description = "AI가 참고할 이미지[현재 사용 불가능]", type = "string", format = "binary")
    private MultipartFile image;
}
