package com.talecraft.talecraftbe.novel.episode.like.service;

import com.talecraft.talecraftbe.novel.episode.like.dto.response.LikeListResponseDTO;
import com.talecraft.talecraftbe.novel.episode.like.model.entity.Like;
import com.talecraft.talecraftbe.novel.episode.like.repository.LikeRepository;
import com.talecraft.talecraftbe.novel.episode.model.entity.EpisodeEntity;
import com.talecraft.talecraftbe.novel.episode.repository.EpisodeRepository;
import com.talecraft.talecraftbe.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LikeService {
    private final EpisodeRepository episodeRepository;
    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository, EpisodeRepository episodeRepository) {
        this.likeRepository = likeRepository;
        this.episodeRepository = episodeRepository;
    }

    @Transactional
    public LikeListResponseDTO addLike(User user, long episodeId) {
        EpisodeEntity episode = episodeRepository.findById(episodeId).orElseThrow(
                () -> new IllegalArgumentException("회차 아이디를 찾을 수 없습니다! " + episodeId)
        );
        likeRepository.findByUserAndEpisode_EpisodesId(user, episodeId).ifPresent(like -> {
            throw new IllegalArgumentException("이미 좋아요를 누른 회차입니다.");
        });

        Like like = new Like();
        like.setUser(user);
        like.setEpisode(episode);

        Like save = likeRepository.save(like);

        return new LikeListResponseDTO(List.of(like.getLikeId()),"좋아요 등록에 성공했습니다.");
    }

    @Transactional
    public LikeListResponseDTO deleteLike(User user, long episodeId) {
        likeRepository.findByUserAndEpisode_EpisodesId(user, episodeId).ifPresent(likeRepository::delete);

        return new LikeListResponseDTO(null, "좋아요 삭제에 성공했습니다.");
    }

    @Transactional(readOnly = true)
    public LikeListResponseDTO getLike(User user, long novelId) {
        List<Like> likeList = likeRepository.findAllByUserAndEpisode_Novel_NovelId(user, novelId);

        if(likeList.isEmpty())
            return new LikeListResponseDTO(null, "해당 작품의 좋아요한 목록이 없습니다.");

        List<Long> likeIdList = likeList.stream().map(Like::getLikeId).toList();

        return new LikeListResponseDTO(likeIdList, "해당 작품의 좋아요한 목록을 조회합니다.");
    }
}
