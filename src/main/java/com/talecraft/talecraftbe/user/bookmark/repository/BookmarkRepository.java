package com.talecraft.talecraftbe.user.bookmark.repository;

import com.talecraft.talecraftbe.user.bookmark.model.entity.Bookmark;
import com.talecraft.talecraftbe.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findAllByUser(User user);

    Optional<Bookmark> findByUserAndNovel_NovelId(User user, Long novelId);
    
    void deleteByUser(User user);
}
