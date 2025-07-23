package com.talecraft.talecraftbe.novel.episode.controller;



import com.talecraft.talecraftbe.novel.episode.dto.request.RequestPostEpisodeDto;
import com.talecraft.talecraftbe.novel.episode.dto.response.ResponsePostEpisodeDto;
import com.talecraft.talecraftbe.novel.episode.service.EpisodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController("/api/novels/{novelId}")
public class EpisodeController {
  private final EpisodeService episodeService;

  public EpisodeController(EpisodeService episodeService) {
      this.episodeService = episodeService;
  }

  @PostMapping
  public ResponseEntity<ResponsePostEpisodeDto> postEpisode(@PathVariable long novelId, @RequestBody RequestPostEpisodeDto requestPostEpisodeDto) {
      //입력값 검증 로직 추가예정.
      return episodeService.createEpisode(novelId,requestPostEpisodeDto);
  }



}
