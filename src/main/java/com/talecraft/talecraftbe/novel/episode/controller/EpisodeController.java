package com.talecraft.talecraftbe.novel.episode.controller;



import com.talecraft.talecraftbe.novel.episode.dto.request.RequestPostEpisodeDto;
import com.talecraft.talecraftbe.novel.episode.dto.request.RequestUpdateEpisodeDto;
import com.talecraft.talecraftbe.novel.episode.dto.response.*;
import com.talecraft.talecraftbe.novel.episode.service.EpisodeService;
import com.talecraft.talecraftbe.user.entity.User;
import org.hibernate.annotations.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


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
      return episodeService.createEpisode(novelId,requestPostEpisodeDto,user);
  }

  //단건 조회
  @GetMapping("/{episodeId}")
  public ResponseEntity<ResponseGetEpisodeDto> getEpisode(@PathVariable long episodeId,@PathVariable long novelId) {
      return episodeService.getEpisode(episodeId,novelId);
  }

  //복수 조회
  @GetMapping
  public ResponseEntity<ResponseGetEpisodeListDto> getEpisode() {
      return episodeService.getEpisodeList();
  }

  //에피소드 수정
  @PatchMapping("/{episodeId}")
  public ResponseEntity<ResponseUpdateEpisodeDto> updateEpisode(@RequestBody RequestUpdateEpisodeDto requestUpdateEpisodeDto, @PathVariable long episodeId, @AuthenticationPrincipal User user) {
      return episodeService.updateEpisode(requestUpdateEpisodeDto,episodeId);
  }

  @DeleteMapping("/{episodeId}")
    public ResponseEntity<ResponseDeleteEpisodeDto> deleteEpisode(@PathVariable long episodeId) {
      return episodeService.deleteEpisode(episodeId);
  }
}
