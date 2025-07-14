package com.talecraft.talecraftbe.novel.chapter.repository;

import com.talecraft.talecraftbe.novel.chapter.model.entity.ChapterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterRepository extends JpaRepository<ChapterEntity, Long> {
}
