package com.talecraft.talecraftbe.novel.repository;

import com.talecraft.talecraftbe.novel.model.entity.NovelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelRepository extends JpaRepository< NovelEntity, Long> {
}
