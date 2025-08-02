package com.talecraft.talecraftbe.user.bookmark.controller;

import com.talecraft.talecraftbe.user.bookmark.dto.response.BookmarkListResponseDTO;
import com.talecraft.talecraftbe.user.bookmark.service.BookmarkService;
import com.talecraft.talecraftbe.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/novels/bookmarks")
public class BookmarkController {
    private BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping
    public ResponseEntity<?> getAllBookmarks(@AuthenticationPrincipal User user) {
        BookmarkListResponseDTO responseDTO = bookmarkService.getBookmark(user);

        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/check/{novelId}")
    public ResponseEntity<?> checkBookmarkStatus(@PathVariable long novelId, @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                log.warn("사용자가 인증되지 않음: novelId={}", novelId);
                Map<String, Object> response = new HashMap<>();
                response.put("isBookmarked", false);
                response.put("error", "로그인이 필요합니다.");
                return ResponseEntity.ok().body(response);
            }
            
            log.info("북마크 상태 확인 요청: novelId={}, userId={}", novelId, user.getId());
            log.info("사용자 정보: {}", user);
            boolean isBookmarked = bookmarkService.isBookmarked(novelId, user);
            log.info("북마크 상태 확인 결과: novelId={}, isBookmarked={}", novelId, isBookmarked);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isBookmarked", isBookmarked);
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("북마크 상태 확인 중 오류 발생: novelId={}, userId={}", novelId, user != null ? user.getId() : "null", e);
            Map<String, Object> response = new HashMap<>();
            response.put("isBookmarked", false);
            response.put("error", "북마크 상태 확인에 실패했습니다.");
            return ResponseEntity.ok().body(response);
        }
    }

    @PostMapping("{novelId}")
    public ResponseEntity<?> addBookmark(@PathVariable long novelId, @AuthenticationPrincipal User user) {
        String userId = user.getId();

        synchronized (userId.intern()) {
            Long bookmarkId = bookmarkService.addBookmark(novelId, user);

            Map<String, Object> response = new HashMap<>();
            response.put("bookmarkId", bookmarkId);
            response.put("message", "북마크가 등록되었습니다.");

            return ResponseEntity.ok().body(response);
        }
    }

    @DeleteMapping("{novelId}")
    public ResponseEntity<?> deleteBookmark(@PathVariable long novelId, @AuthenticationPrincipal User user) {
        bookmarkService.deleteBookmark(novelId, user);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("message", "북마크가 등록되었습니다.");

        return ResponseEntity.ok().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleException(IllegalArgumentException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}