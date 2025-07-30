package com.talecraft.talecraftbe.ai.dto.data;

import com.talecraft.talecraftbe.ai.exception.AIRequestFailException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum AIOptions {
    NORMAL("기본 설정",1),
    SPELL_CHECK("오타 검사", 2),
    STORY_EXTENSION("이야기 늘리기", 3),
    MAKE_NAME("이름 추천", 4);

    private String option; // AI 연결 옵션
    private int optionNumber; // AI 연결 옵션 번호

    public static String getAIURL(AIOptions options, String url) {
        switch (options) {
            case NORMAL:
                return url + "/api/chat";
            case SPELL_CHECK:
                return url + "/api/spell-check";
            case STORY_EXTENSION:
                 return url + "/api/extension";
            case MAKE_NAME:
                return url + "/api/make-name";
            default:
                throw new AIRequestFailException("존재하지 않는 ENUM 값입니다!");
        }
    }
}
