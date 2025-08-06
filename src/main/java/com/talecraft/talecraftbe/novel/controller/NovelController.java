package com.talecraft.talecraftbe.novel.controller;


import com.talecraft.talecraftbe.novel.dto.request.RequestPatchNovelDto;
import com.talecraft.talecraftbe.novel.dto.request.RequestPostNovelDto;
import com.talecraft.talecraftbe.novel.dto.response.*;
import com.talecraft.talecraftbe.novel.service.NovelService;
import com.talecraft.talecraftbe.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/novels")
@CrossOrigin(origins = "http://localhost:3000")
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
        //본인만수정하게하세요
        return novelService.updateNovel(requestPatchNovelDto,novelId,user);
    }

    @DeleteMapping("/{novelId}")
    public ResponseEntity<ResponseDeleteNovelDto> deleteNovel(@PathVariable long novelId, @AuthenticationPrincipal User user) {
        return novelService.deleteNovel(novelId,user);
    }

    //단건 조회
    @GetMapping("/{novelId}")
    public ResponseEntity<ResponseGetNovelDto> getNovel(@PathVariable long novelId,@AuthenticationPrincipal User user) {
        return novelService.getNovel(novelId,user);
    }

    //내 작품 단건 조회
    @GetMapping("/{novelId}/my")
    public ResponseEntity<ResponseGetNovelDto> getMyNovel(@PathVariable long novelId,@AuthenticationPrincipal User user) {
        return novelService.getMyNovel(novelId,user);
    }

    //전체 조회
    @GetMapping()
    public ResponseEntity<ResponseGetNovelListDto> getAllNovels(@RequestParam(value = "value",required = false) String keyword,@RequestParam(value="type", required=false) String type) {
            return novelService.getNovelList(keyword,type);
    }

    //내 작품 전체 조회
    @GetMapping("/my")
    public ResponseEntity<ResponseGetNovelListDto> getMyAllNovels(@AuthenticationPrincipal User user) {
        return novelService.getMyNovelList(user);
    }

    // 소설 차단/해제 (관리자용)
    @PatchMapping("/{novelId}/ban")
    public ResponseEntity<?> toggleNovelBan(@PathVariable long novelId, @AuthenticationPrincipal User user) {
        return novelService.toggleNovelBan(novelId, user);
    }

}
