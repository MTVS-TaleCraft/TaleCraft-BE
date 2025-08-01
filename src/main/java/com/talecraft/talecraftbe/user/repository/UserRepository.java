package com.talecraft.talecraftbe.user.repository;

import com.talecraft.talecraftbe.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findById(String id);
    Optional<User> findByEmailAndId(String email, String id);
    List<User> findByAuthorityIdNot(Long authorityId);
}