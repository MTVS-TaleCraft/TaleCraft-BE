package com.talecraft.talecraftbe.user.service;

import com.talecraft.talecraftbe.user.entity.User;
import com.talecraft.talecraftbe.user.repository.UserRepository;
import com.talecraft.talecraftbe.user.bookmark.repository.BookmarkRepository;
import com.talecraft.talecraftbe.novel.repository.NovelRepository;
import com.talecraft.talecraftbe.novel.episode.repository.EpisodeRepository;
import com.talecraft.talecraftbe.inquiry.repository.InquiryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final NovelRepository novelRepository;
    private final EpisodeRepository episodeRepository;
    private final InquiryRepository inquiryRepository;
    
    public UserService(UserRepository userRepository, 
                      BookmarkRepository bookmarkRepository,
                      NovelRepository novelRepository,
                      EpisodeRepository episodeRepository,
                      InquiryRepository inquiryRepository) {
        this.userRepository = userRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.novelRepository = novelRepository;
        this.episodeRepository = episodeRepository;
        this.inquiryRepository = inquiryRepository;
    }
    
    /**
     * 회원탈퇴 처리
     * @param user 탈퇴할 사용자
     */
    public void withdrawUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보가 없습니다.");
        }
        
        log.info("회원탈퇴 처리 시작: userId={}", user.getId());
        
        try {
            // 1. 사용자의 북마크 삭제
            bookmarkRepository.deleteByUser(user);
            log.info("북마크 삭제 완료: userId={}", user.getId());
            
            // 2. 사용자의 문의사항 삭제
            inquiryRepository.deleteByUser(user);
            log.info("문의사항 삭제 완료: userId={}", user.getId());
            
            // 3. 사용자의 소설과 에피소드 삭제
            var userNovels = novelRepository.findAllByUser(user);
            for (var novel : userNovels) {
                // 소설의 에피소드들을 먼저 삭제
                var episodes = episodeRepository.findByNovelNovelIdAndIsDeletedFalse(novel.getNovelId());
                episodeRepository.deleteAll(episodes);
                log.info("에피소드 삭제 완료: novelId={}, episodeCount={}", novel.getNovelId(), episodes.size());
            }
            novelRepository.deleteAll(userNovels);
            log.info("소설 삭제 완료: userId={}", user.getId());
            
            // 4. 사용자 삭제
            userRepository.delete(user);
            
            log.info("회원탈퇴 완료: userId={}", user.getId());
        } catch (Exception e) {
            log.error("회원탈퇴 처리 중 오류 발생: userId={}", user.getId(), e);
            throw new RuntimeException("회원탈퇴 처리 중 오류가 발생했습니다.", e);
        }
    }
} 