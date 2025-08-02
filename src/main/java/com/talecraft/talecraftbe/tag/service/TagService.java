package com.talecraft.talecraftbe.tag.service;

import com.talecraft.talecraftbe.tag.dto.response.TagListResponseDTO;
import com.talecraft.talecraftbe.tag.dto.response.TagResponseDTO;
import com.talecraft.talecraftbe.tag.model.entity.Tag;
import com.talecraft.talecraftbe.tag.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class TagService {
    
    private final TagRepository tagRepository;
    
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
    
    // 작품에 태그 추가
    public void addTags(Long novelId, List<String> tagNames) {
        log.info("Adding tags to novel {}: {}", novelId, tagNames);
        
        for (String tagName : tagNames) {
            if (tagName != null && !tagName.trim().isEmpty()) {
                // 이미 존재하는 태그인지 확인
                if (!tagRepository.existsByNovelIdAndTagName(novelId, tagName.trim())) {
                    Tag tag = new Tag();
                    tag.setNovelId(novelId);
                    tag.setTagName(tagName.trim());
                    tag.setTagId(generateTagId()); // 태그 ID 생성
                    tagRepository.save(tag);
                    log.info("Tag added: {} for novel {}", tagName.trim(), novelId);
                } else {
                    log.info("Tag already exists: {} for novel {}", tagName.trim(), novelId);
                }
            }
        }
    }
    
    // 작품의 태그 목록 조회
    @Transactional(readOnly = true)
    public TagResponseDTO getTagsByNovelId(Long novelId) {
        log.info("Getting tags for novel: {}", novelId);
        
        List<Tag> tags = tagRepository.findByNovelId(novelId);
        List<String> tagNames = tags.stream()
                .map(Tag::getTagName)
                .collect(Collectors.toList());
        
        TagResponseDTO responseDTO = new TagResponseDTO();
        responseDTO.setNovelId(novelId);
        responseDTO.setTagNames(tagNames);
        responseDTO.setTagCount(tagNames.size());
        
        return responseDTO;
    }
    
    // 태그명으로 작품 검색
    @Transactional(readOnly = true)
    public List<Long> searchNovelsByTag(String tagName) {
        log.info("Searching novels by tag: {}", tagName);
        return tagRepository.findNovelIdsByTagNameContaining(tagName);
    }
    
    // 태그 삭제
    public void deleteTag(Long novelId, String tagName) {
        log.info("Deleting tag: {} from novel: {}", tagName, novelId);
        
        Tag.TagId tagId = new Tag.TagId();
        tagId.setNovelId(novelId);
        tagId.setTagId(findTagIdByNovelIdAndTagName(novelId, tagName));
        
        if (tagId.getTagId() != null) {
            tagRepository.deleteById(tagId);
            log.info("Tag deleted: {} from novel {}", tagName, novelId);
        } else {
            log.warn("Tag not found: {} for novel {}", tagName, novelId);
        }
    }
    
    // 작품의 모든 태그 삭제
    public void deleteAllTagsByNovelId(Long novelId) {
        log.info("Deleting all tags for novel: {}", novelId);
        List<Tag> tags = tagRepository.findByNovelId(novelId);
        tagRepository.deleteAll(tags);
        log.info("All tags deleted for novel: {}", novelId);
    }
    
    // 태그명으로 태그 검색
    @Transactional(readOnly = true)
    public List<Tag> searchTagsByTagName(String tagName) {
        log.info("Searching tags by name: {}", tagName);
        return tagRepository.findByTagNameContaining(tagName);
    }
    
    // 태그 존재 여부 확인
    @Transactional(readOnly = true)
    public boolean isTagExists(Long novelId, String tagName) {
        log.info("Checking if tag exists: {} for novel: {}", tagName, novelId);
        return tagRepository.existsByNovelIdAndTagName(novelId, tagName);
    }
    
    // 기본 태그 목록 조회 (novelId = 0인 태그들)
    @Transactional(readOnly = true)
    public List<String> getCommonTags() {
        log.info("Getting common tags");
        List<Tag> commonTags = tagRepository.findByNovelId(0L);
        return commonTags.stream()
                .map(Tag::getTagName)
                .collect(Collectors.toList());
    }
    
    // 기본 태그를 작품에 추가
    public void addCommonTagToNovel(Long novelId, String tagName) {
        log.info("Adding common tag: {} to novel: {}", tagName, novelId);
        
        // 기본 태그가 존재하는지 확인
        if (!tagRepository.existsByNovelIdAndTagName(0L, tagName)) {
            throw new IllegalArgumentException("기본 태그가 존재하지 않습니다: " + tagName);
        }
        
        // 이미 작품에 해당 태그가 있는지 확인
        if (tagRepository.existsByNovelIdAndTagName(novelId, tagName)) {
            log.info("Tag already exists: {} for novel {}", tagName, novelId);
            return;
        }
        
        // 기본 태그를 작품에 추가
        Tag tag = new Tag();
        tag.setNovelId(novelId);
        tag.setTagName(tagName);
        tag.setTagId(generateTagId());
        tagRepository.save(tag);
        
        log.info("Common tag added: {} to novel {}", tagName, novelId);
    }
    
    // 태그 ID 생성 (간단한 구현)
    private Long generateTagId() {
        return System.currentTimeMillis();
    }
    
    // novelId와 tagName으로 tagId 찾기
    private Long findTagIdByNovelIdAndTagName(Long novelId, String tagName) {
        List<Tag> tags = tagRepository.findByNovelId(novelId);
        return tags.stream()
                .filter(tag -> tag.getTagName().equals(tagName))
                .findFirst()
                .map(Tag::getTagId)
                .orElse(null);
    }
} 