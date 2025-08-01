package com.talecraft.talecraftbe.auth.dto;

public record UserDetailResponse(
    String id,
    String userName,
    String email,
    Long authorityId,
    boolean isBlocked,
    boolean isWithdrawn
) {
    public static UserDetailResponse fromUser(com.talecraft.talecraftbe.user.entity.User user) {
        return new UserDetailResponse(
            user.getId(),
            user.getUserName(),
            user.getEmail(),
            user.getAuthorityId(),
            false, // isBlocked - User 엔티티에 해당 필드가 없으므로 기본값
            false  // isWithdrawn - User 엔티티에 해당 필드가 없으므로 기본값
        );
    }
} 