package com.talecraft.talecraftbe.ai.repository;

import com.talecraft.talecraftbe.ai.model.entity.ChatList;
import com.talecraft.talecraftbe.ai.model.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatListRepository extends JpaRepository<ChatList, Long> {
}
