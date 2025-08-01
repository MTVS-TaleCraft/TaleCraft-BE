package com.talecraft.talecraftbe.novel.episode.controller;



import com.talecraft.talecraftbe.novel.episode.dto.request.RequestPostEpisodeDto;
import com.talecraft.talecraftbe.novel.episode.dto.request.RequestUpdateEpisodeDto;
import com.talecraft.talecraftbe.novel.episode.dto.response.*;
import com.talecraft.talecraftbe.novel.episode.service.EpisodeService;
import com.talecraft.talecraftbe.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/novels/{novelId}/episodes")
public class EpisodeController {
  private final EpisodeService episodeService;

  public EpisodeController(EpisodeService episodeService) {
      this.episodeService = episodeService;
  }


  //userId
  @PostMapping
  public ResponseEntity<ResponsePostEpisodeDto> postEpisode(@PathVariable long novelId, @RequestBody RequestPostEpisodeDto requestPostEpisodeDto, @AuthenticationPrincipal User user) {
      //입력값 검증 로직 추가예정.(MVP 완성후)
    log.info("user{}", user);
      return episodeService.createEpisode(novelId,requestPostEpisodeDto,user);
  }

  //단건 조회
  @GetMapping("/{episodeId}")
  public ResponseEntity<ResponseGetEpisodeDto> getEpisode(@PathVariable long episodeId,@PathVariable long novelId) {
      return episodeService.getEpisode(episodeId,novelId);
  }

  //복수 조회
  @GetMapping
  public ResponseEntity<ResponseGetEpisodeListDto> getEpisode(@PathVariable long novelId) {
      return episodeService.getEpisodeListByNovelId(novelId);
  }

  //에피소드 수정
  @PatchMapping("/{episodeId}")
  public ResponseEntity<ResponseUpdateEpisodeDto> updateEpisode(@RequestBody RequestUpdateEpisodeDto requestUpdateEpisodeDto, @PathVariable long episodeId, @AuthenticationPrincipal User user) {
      return episodeService.updateEpisode(requestUpdateEpisodeDto,episodeId,user);
  }

  //에피소드 삭제
  @DeleteMapping("/{episodeId}")
    public ResponseEntity<ResponseDeleteEpisodeDto> deleteEpisode(@PathVariable long episodeId,@AuthenticationPrincipal User user) {
      return episodeService.deleteEpisode(episodeId,user);
  }
}
