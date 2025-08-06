package com.talecraft.talecraftbe.novel.repository;

import com.talecraft.talecraftbe.novel.model.entity.Availability;
import com.talecraft.talecraftbe.novel.model.entity.NovelEntity;
import com.talecraft.talecraftbe.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NovelRepository extends JpaRepository< NovelEntity, Long> {
    NovelEntity findByNovelId(long novelId);

    List<NovelEntity> findAllByUser(User user);

    List<NovelEntity> findAllByTitle(String keyword);

    List<NovelEntity> findAllByUserUserName(String userUserName);

    List<NovelEntity> findAllByUserId(String userId);

    // 차단된 소설 ID 목록 조회
    List<Long> findNovelIdsByIsBannedTrue();

    long countByIsDeleted(boolean b);

    List<NovelEntity> findAllByAvailability(Availability availability);

    List<NovelEntity> findAllByTitleAndAvailability(String title, Availability availability);

    List<NovelEntity> findAllByUserUserNameAndAvailability(String keyword, Availability aPublic);

    List<NovelEntity> findAllByUserIdAndAvailability(String userId, Availability availability);
}
