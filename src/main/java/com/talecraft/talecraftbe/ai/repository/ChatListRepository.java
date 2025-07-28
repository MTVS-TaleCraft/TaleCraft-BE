package com.talecraft.talecraftbe.ai.repository;

import com.talecraft.talecraftbe.ai.model.entity.ChatList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatListRepository extends JpaRepository<ChatList, Long> {
    Optional<ChatList> findByEpisode_EpisodesId(Long episodeEpisodesId);

    List<ChatList> findAllByEpisode_EpisodesId(Long episodeEpisodesId);
}
