package com.talecraft.talecraftbe.novel.episode.like.controller;

import com.talecraft.talecraftbe.novel.episode.like.dto.response.LikeListResponseDTO;
import com.talecraft.talecraftbe.novel.episode.like.service.LikeService;
import com.talecraft.talecraftbe.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/novels/{novelId}/like")
public class LikeController {
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @GetMapping()
    public ResponseEntity<?> getLike(@AuthenticationPrincipal User user, @PathVariable long novelId, @PathVariable long episodeId) {
        log.info("GET : /novels/{}/like", novelId);

        LikeListResponseDTO responseDTO = likeService.getLike(user, novelId);

        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/episodes/{episodeId}")
    public ResponseEntity<?> addLike(@AuthenticationPrincipal User user, @PathVariable Long novelId, @PathVariable long episodeId) {
        log.info("POST : /api/novel/{}/like/episode/{}", novelId, episodeId);

        String userId = user.getId();
        synchronized (userId.intern()) {
            LikeListResponseDTO responseDTO = likeService.addLike(user, episodeId);

            return ResponseEntity.ok().body(responseDTO);
        }
    }

    @DeleteMapping("/episodes/{episodeId}")
    public ResponseEntity<?> deleteLike(@AuthenticationPrincipal User user, @PathVariable Long novelId, @PathVariable long episodeId) {
        log.info("DELETE : /api/novel/{}/like/episode/{}", novelId, episodeId);

        LikeListResponseDTO responseDTO = likeService.deleteLike(user, episodeId);

        return ResponseEntity.ok().body(responseDTO);
    }
}
