package com.talecraft.talecraftbe.user.bookmark.service;

import com.talecraft.talecraftbe.novel.model.entity.NovelEntity;
import com.talecraft.talecraftbe.novel.repository.NovelRepository;
import com.talecraft.talecraftbe.user.bookmark.dto.response.BookmarkListResponseDTO;
import com.talecraft.talecraftbe.user.bookmark.dto.response.BookmarkResponseDTO;
import com.talecraft.talecraftbe.user.bookmark.model.Bookmark;
import com.talecraft.talecraftbe.user.bookmark.repository.BookmarkRepository;
import com.talecraft.talecraftbe.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        Bookmark bookmark = new Bookmark();
        bookmark.setNovel(novelEntity);
        bookmark.setUser(user);

        return bookmarkRepository.save(bookmark).getBookmarkId();
    }

    public BookmarkListResponseDTO getBookmark(User user) {
        List<Bookmark> allByUser = bookmarkRepository.findAllByUser(user);
        List<BookmarkResponseDTO> responseDTOList = allByUser.stream().map(BookmarkResponseDTO::new).toList();

        return new BookmarkListResponseDTO(responseDTOList);
    }

    public void deleteBookmark(long novelId, User user) {
        Optional<Bookmark> findBookmark = bookmarkRepository.findByUserAndNovel_NovelId(user, novelId);
        if (findBookmark.isPresent()) {
            bookmarkRepository.delete(findBookmark.get());
        } else {
            throw new IllegalArgumentException("북마크를 삭제하는데 실패했습니다!");
        }
    }
}
