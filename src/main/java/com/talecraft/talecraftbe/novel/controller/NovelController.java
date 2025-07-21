package com.talecraft.talecraftbe.novel.controller;


import com.talecraft.talecraftbe.novel.dto.request.RequestPostNovelDto;
import com.talecraft.talecraftbe.novel.dto.response.ResponsePostNovelDto;
import com.talecraft.talecraftbe.novel.service.NovelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/novels")
public class NovelController {
    private final NovelService novelService;

    @Autowired
    public NovelController(NovelService novelService) {
        this.novelService = novelService;
    }
//유저 나중에 넣으세요
    public ResponseEntity<ResponsePostNovelDto> postNovel( @RequestBody RequestPostNovelDto requestPostNovelDto) {
        novelService.createNovel(requestPostNovelDto);
    }
}
