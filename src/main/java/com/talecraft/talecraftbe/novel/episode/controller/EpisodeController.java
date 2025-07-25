package com.talecraft.talecraftbe.novel.episode.controller;



import com.talecraft.talecraftbe.novel.episode.dto.request.RequestPostEpisodeDto;
import com.talecraft.talecraftbe.novel.episode.dto.response.ResponseDeleteEpisodeDto;
import com.talecraft.talecraftbe.novel.episode.dto.response.ResponseGetEpisodeDto;
import com.talecraft.talecraftbe.novel.episode.dto.response.ResponseGetEpisodeListDto;
import com.talecraft.talecraftbe.novel.episode.dto.response.ResponsePostEpisodeDto;
import com.talecraft.talecraftbe.novel.episode.service.EpisodeService;
import org.hibernate.annotations.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/novels/{novelId}/episodes")
public class EpisodeController {
  private final EpisodeService episodeService;

  public EpisodeController(EpisodeService episodeService) {
      this.episodeService = episodeService;
  }

  @PostMapping
  public ResponseEntity<ResponsePostEpisodeDto> postEpisode(@PathVariable long novelId, @RequestBody RequestPostEpisodeDto requestPostEpisodeDto) {
      //입력값 검증 로직 추가예정.(MVP 완성후)
      return episodeService.createEpisode(novelId,requestPostEpisodeDto);
  }

  //단건 조회
  @GetMapping("/{episodesId}")
  public ResponseEntity<ResponseGetEpisodeDto> getEpisode(@PathVariable long episodeId,@PathVariable long novelId) {
      return episodeService.getEpisode(episodeId,novelId);
  }

  //복수 조회
  @GetMapping
  public ResponseEntity<ResponseGetEpisodeListDto> getEpisode() {
      return episodeService.getEpisodeList();
  }

  //에피소드 수정
  @PatchMapping("/{episodesId}")
  public ResponseEntity<ResponsePostEpisodeDto> updateEpisode(@RequestBody RequestPostEpisodeDto requestPostEpisodeDto,@PathVariable long episodeId) {
      return episodeService.updateEpisode(requestPostEpisodeDto,episodeId);
  }

  @DeleteMapping("/{episodeId}")
    public ResponseEntity<ResponseDeleteEpisodeDto> deleteEpisode(@PathVariable long episodeId) {
      return episodeService.deleteEpisode(episodeId);
  }
}
