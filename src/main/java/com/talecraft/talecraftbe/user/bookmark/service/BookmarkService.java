package com.talecraft.talecraftbe.user.bookmark.service;

import com.talecraft.talecraftbe.novel.model.entity.NovelEntity;
import com.talecraft.talecraftbe.novel.repository.NovelRepository;
import com.talecraft.talecraftbe.user.bookmark.dto.response.BookmarkListResponseDTO;
import com.talecraft.talecraftbe.user.bookmark.dto.response.BookmarkResponseDTO;
import com.talecraft.talecraftbe.user.bookmark.model.entity.Bookmark;
import com.talecraft.talecraftbe.user.bookmark.repository.BookmarkRepository;
import com.talecraft.talecraftbe.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BookmarkService {
    private BookmarkRepository bookmarkRepository;
    private NovelRepository novelRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository, NovelRepository novelRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.novelRepository = novelRepository;
    }

    @Transactional
    public Long addBookmark(long novelId, User user) {
        NovelEntity novelEntity = novelRepository.findById(novelId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 소설ID입니다!: " + novelId)
        );
        bookmarkRepository.findByUserAndNovel_NovelId(user, novelId).ifPresent(bookmark -> {
            throw new IllegalArgumentException("이미 북마크된 작품입니다!");
        });

        Bookmark bookmark = new Bookmark();
        bookmark.setNovel(novelEntity);
        bookmark.setUser(user);

        return bookmarkRepository.save(bookmark).getBookmarkId();
    }

    @Transactional(readOnly = true)
    public BookmarkListResponseDTO getBookmark(User user) {
        log.info("북마크 조회 서비스 호출: userId={}", user.getId());
        
        List<Bookmark> allByUser = bookmarkRepository.findAllByUser(user);
        log.info("데이터베이스에서 조회된 북마크 수: {}", allByUser.size());
        
        List<BookmarkResponseDTO> responseDTOList = allByUser.stream().map(BookmarkResponseDTO::new).toList();
        log.info("변환된 북마크 DTO 수: {}", responseDTOList.size());

        return new BookmarkListResponseDTO(responseDTOList);
    }

    @Transactional
    public void deleteBookmark(long novelId, User user) {
        Optional<Bookmark> findBookmark = bookmarkRepository.findByUserAndNovel_NovelId(user, novelId);
        if (findBookmark.isPresent()) {
            bookmarkRepository.delete(findBookmark.get());
        } else {
            throw new IllegalArgumentException("북마크를 삭제하는데 실패했습니다!");
        }
    }

    @Transactional(readOnly = true)
    public boolean isBookmarked(long novelId, User user) {
        try {
            log.info("북마크 상태 확인 중: novelId={}, userId={}", novelId, user.getId());
            Optional<Bookmark> bookmark = bookmarkRepository.findByUserAndNovel_NovelId(user, novelId);
            boolean exists = bookmark.isPresent();
            log.info("북마크 조회 결과: novelId={}, userId={}, exists={}", novelId, user.getId(), exists);
            return exists;
        } catch (Exception e) {
            log.error("북마크 상태 확인 중 오류 발생: novelId={}, userId={}", novelId, user.getId(), e);
            return false;
        }
    }
}
