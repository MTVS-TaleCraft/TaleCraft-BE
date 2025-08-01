package com.talecraft.talecraftbe.novel.episode.repository;

import com.talecraft.talecraftbe.novel.episode.model.entity.EpisodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EpisodeRepository extends JpaRepository<EpisodeEntity, Long> {
    List<EpisodeEntity> findByNovelNovelIdAndIsDeletedFalse(Long novelId);
}
