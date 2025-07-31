package com.talecraft.talecraftbe.bookmark.service;

import com.talecraft.talecraftbe.bookmark.dto.response.BookmarkListResponseDTO;
import com.talecraft.talecraftbe.bookmark.model.Bookmark;
import com.talecraft.talecraftbe.bookmark.repository.BookmarkRepository;
import com.talecraft.talecraftbe.novel.repository.NovelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class BookmarkService {
    
    private final BookmarkRepository bookmarkRepository;
    private final NovelRepository novelRepository;
    
    public BookmarkService(BookmarkRepository bookmarkRepository, NovelRepository novelRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.novelRepository = novelRepository;
    }
    
    // 북마크 추가
    public Long addBookmark(Long novelId, String userId) {
        log.info("Adding bookmark for novel: {} and user: {}", novelId, userId);
        
        // 작품이 존재하는지 확인
        if (!novelRepository.existsById(novelId)) {
            throw new IllegalArgumentException("작품을 찾을 수 없습니다!");
        }
        
        // 이미 북마크가 있는지 확인
        if (bookmarkRepository.existsByUserIdAndNovel_NovelId(userId, novelId)) {
            throw new IllegalArgumentException("이미 북마크가 등록된 작품입니다!");
        }
        
        // 북마크 생성
        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(userId);
        bookmark.setNovel(novelRepository.findById(novelId).orElse(null));
        
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);
        log.info("Bookmark added successfully: {}", savedBookmark.getBookmarkId());
        
        return savedBookmark.getBookmarkId();
    }
    
    // 사용자의 북마크 목록 조회
    @Transactional(readOnly = true)
    public BookmarkListResponseDTO getBookmarks(String userId) {
        log.info("Getting bookmarks for user: {}", userId);
        
        List<Bookmark> bookmarks = bookmarkRepository.findAllByUserId(userId);
        
        BookmarkListResponseDTO responseDTO = new BookmarkListResponseDTO();
        responseDTO.setBookmarks(bookmarks.stream()
                .map(bookmark -> {
                    BookmarkListResponseDTO.BookmarkDTO bookmarkDTO = new BookmarkListResponseDTO.BookmarkDTO();
                    bookmarkDTO.setBookmarkId(bookmark.getBookmarkId());
                    bookmarkDTO.setNovelId(bookmark.getNovel().getNovelId());
                    bookmarkDTO.setNovelTitle(bookmark.getNovel().getTitle());
                    return bookmarkDTO;
                })
                .collect(Collectors.toList()));
        responseDTO.setTotalCount(bookmarks.size());
        
        return responseDTO;
    }
    
    // 북마크 삭제
    public void deleteBookmark(Long novelId, String userId) {
        log.info("Deleting bookmark for novel: {} and user: {}", novelId, userId);
        
        Bookmark bookmark = bookmarkRepository.findByUserIdAndNovel_NovelId(userId, novelId);
        if (bookmark == null) {
            throw new IllegalArgumentException("북마크를 찾을 수 없습니다!");
        }
        
        bookmarkRepository.delete(bookmark);
        log.info("Bookmark deleted successfully");
    }
    
    // 북마크 존재 여부 확인
    @Transactional(readOnly = true)
    public boolean isBookmarked(Long novelId, String userId) {
        return bookmarkRepository.existsByUserIdAndNovel_NovelId(userId, novelId);
    }
}
