package com.talecraft.talecraftbe.novel.repository;

import com.talecraft.talecraftbe.novel.model.entity.NovelEntity;
import com.talecraft.talecraftbe.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NovelRepository extends JpaRepository< NovelEntity, Long> {
    NovelEntity findByNovelId(long novelId);

    List<NovelEntity> findAllByUser(User user);
}
