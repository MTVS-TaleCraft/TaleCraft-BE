package com.talecraft.talecraftbe.bookmark.controller;

import com.talecraft.talecraftbe.bookmark.dto.response.BookmarkListResponseDTO;
import com.talecraft.talecraftbe.bookmark.service.BookmarkService;
import com.talecraft.talecraftbe.auth.config.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    
    private final BookmarkService bookmarkService;
    private final JwtProvider jwtProvider;
    
    public BookmarkController(BookmarkService bookmarkService, JwtProvider jwtProvider) {
        this.bookmarkService = bookmarkService;
        this.jwtProvider = jwtProvider;
    }
    
    @GetMapping
    public ResponseEntity<?> getAllBookmarks(@AuthenticationPrincipal UserDetails userDetails) {
        // UserDetails.getUsername()은 실제로 사용자 로그인 ID를 반환함
        String userId = userDetails.getUsername();
        log.info("Getting bookmarks for user ID: {}", userId);
        BookmarkListResponseDTO responseDTO = bookmarkService.getBookmarks(userId);
        return ResponseEntity.ok().body(responseDTO);
    }
    
    @PostMapping("/{novelId}")
    public ResponseEntity<?> addBookmark(@PathVariable Long novelId, @AuthenticationPrincipal UserDetails userDetails) {
        // UserDetails.getUsername()은 실제로 사용자 로그인 ID를 반환함
        String userId = userDetails.getUsername();
        log.info("Adding bookmark for user ID: {} and novel: {}", userId, novelId);
        Long bookmarkId = bookmarkService.addBookmark(novelId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("bookmarkId", bookmarkId);
        response.put("message", "북마크가 등록되었습니다.");
        
        return ResponseEntity.ok().body(response);
    }
    
    @DeleteMapping("/{novelId}")
    public ResponseEntity<?> deleteBookmark(@PathVariable Long novelId, @AuthenticationPrincipal UserDetails userDetails) {
        // UserDetails.getUsername()은 실제로 사용자 로그인 ID를 반환함
        String userId = userDetails.getUsername();
        log.info("Deleting bookmark for user ID: {} and novel: {}", userId, novelId);
        bookmarkService.deleteBookmark(novelId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("message", "북마크가 삭제되었습니다.");
        
        return ResponseEntity.ok().body(response);
    }
    
    @GetMapping("/{novelId}/check")
    public ResponseEntity<?> checkBookmark(@PathVariable Long novelId, @AuthenticationPrincipal UserDetails userDetails) {
        // UserDetails.getUsername()은 실제로 사용자 로그인 ID를 반환함
        String userId = userDetails.getUsername();
        log.info("Checking bookmark for user ID: {} and novel: {}", userId, novelId);
        boolean isBookmarked = bookmarkService.isBookmarked(novelId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("isBookmarked", isBookmarked);
        
        return ResponseEntity.ok().body(response);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleException(IllegalArgumentException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
