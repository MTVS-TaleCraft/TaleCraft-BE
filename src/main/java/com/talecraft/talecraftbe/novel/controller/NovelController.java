package com.talecraft.talecraftbe.novel.controller;


import com.talecraft.talecraftbe.novel.dto.request.RequestPatchNovelDto;
import com.talecraft.talecraftbe.novel.dto.request.RequestPostNovelDto;
import com.talecraft.talecraftbe.novel.dto.response.ResponseDeleteNovelDto;
import com.talecraft.talecraftbe.novel.dto.response.ResponsePatchNovelDto;
import com.talecraft.talecraftbe.novel.dto.response.ResponsePostNovelDto;
import com.talecraft.talecraftbe.novel.service.NovelService;
import com.talecraft.talecraftbe.user.entity.User;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController("/api/novels")
public class NovelController {
    private final NovelService novelService;

    @Autowired
    public NovelController(NovelService novelService) {
        this.novelService = novelService;
    }

    @PostMapping("/add")
    public ResponseEntity<ResponsePostNovelDto> postNovel(@RequestBody RequestPostNovelDto requestPostNovelDto, @AuthenticationPrincipal User user) {
        return novelService.createNovel(requestPostNovelDto,user);
    }

    @PatchMapping("/{novelId}")
    public ResponseEntity<ResponsePatchNovelDto> patchNovel(@PathVariable long novelId, @RequestBody RequestPatchNovelDto requestPatchNovelDto, @AuthenticationPrincipal User user) {
        return novelService.updateNovel(requestPatchNovelDto,novelId,user);
    }

    @DeleteMapping("/{novelId}")
    public ResponseEntity<ResponseDeleteNovelDto> deleteNovel(@PathVariable long novelId, @AuthenticationPrincipal User user) {
        return novelService.deleteNovel(novelId,user);
    }
}
