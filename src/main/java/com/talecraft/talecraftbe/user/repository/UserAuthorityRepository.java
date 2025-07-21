package com.talecraft.talecraftbe.user.repository;

import com.talecraft.talecraftbe.user.entity.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {
} 