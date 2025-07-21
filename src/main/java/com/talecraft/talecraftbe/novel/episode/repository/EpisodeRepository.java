package com.talecraft.talecraftbe.novel.episode.repository;

import com.talecraft.talecraftbe.novel.episode.model.entity.EpisodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodeRepository extends JpaRepository<EpisodeEntity, Long> {
}
