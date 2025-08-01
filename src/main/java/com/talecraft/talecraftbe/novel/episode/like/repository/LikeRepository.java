package com.talecraft.talecraftbe.novel.episode.like.repository;

import com.talecraft.talecraftbe.novel.episode.like.model.entity.Like;
import com.talecraft.talecraftbe.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndEpisode_EpisodesId(User user, Long episodeEpisodesId);

    List<Like> findAllByUserAndEpisode_Novel_NovelId(User user, long episodeNovelNovelId);
}
