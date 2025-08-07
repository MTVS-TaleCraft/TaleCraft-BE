package com.talecraft.talecraftbe.tag.service;

import com.talecraft.talecraftbe.novel.model.entity.Availability;
import com.talecraft.talecraftbe.tag.model.entity.Tag;
import com.talecraft.talecraftbe.tag.model.entity.NovelTag;
import com.talecraft.talecraftbe.tag.repository.TagRepository;
import com.talecraft.talecraftbe.tag.repository.NovelTagRepository;
import com.talecraft.talecraftbe.tag.dto.request.TagRequestDTO;
import com.talecraft.talecraftbe.tag.dto.response.TagResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import com.talecraft.talecraftbe.novel.repository.NovelRepository;
import com.talecraft.talecraftbe.novel.model.entity.NovelEntity;
import com.talecraft.talecraftbe.novel.dto.response.ResponseGetNovelDto;

@Slf4j
@Service
public class TagService {
    private final TagRepository tagRepository;
    private final NovelTagRepository novelTagRepository;
    private final NovelRepository novelRepository;
    
    public TagService(TagRepository tagRepository, NovelTagRepository novelTagRepository, NovelRepository novelRepository) {
        this.tagRepository = tagRepository;
        this.novelTagRepository = novelTagRepository;
        this.novelRepository = novelRepository;
    }
    
    // 작품에 태그 추가 (요청자 정보 포함)
    public void addTags(Long novelId, List<String> tagNames, String requesterType, String requesterId) {
        log.info("Adding tags to novel {}: {} by {} (ID: {})", novelId, tagNames, requesterType, requesterId);
        
        for (String tagName : tagNames) {
            if (tagName != null && !tagName.trim().isEmpty()) {
                String trimmedTagName = tagName.trim();
                
                // 1. 태그가 존재하는지 확인, 없으면 생성
                Tag tag = tagRepository.findByTagName(trimmedTagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setTagName(trimmedTagName);
                            return tagRepository.save(newTag);
                        });
                
                // 2. 작품과 태그 연결이 이미 존재하는지 확인
                if (!novelTagRepository.existsByNovelIdAndTagName(novelId, trimmedTagName)) {
                    NovelTag novelTag = new NovelTag();
                    novelTag.setNovelId(novelId);
                    novelTag.setTagId(tag.getTagId());
                    novelTagRepository.save(novelTag);
                    log.info("Tag added: {} for novel {} by {} (ID: {})", trimmedTagName, novelId, requesterType, requesterId);
                } else {
                    log.info("Tag already exists: {} for novel {}", trimmedTagName, novelId);
                }
            }
        }
    }
    
    // 작품에 태그 추가 (기존 호환성을 위한 오버로드)
    public void addTags(Long novelId, List<String> tagNames) {
        addTags(novelId, tagNames, "UNKNOWN", "UNKNOWN");
    }
    
    // 작품의 태그 목록 조회
    @Transactional(readOnly = true)
    public TagResponseDTO.NovelTagsResponse getTagsByNovelId(Long novelId) {
        log.info("Getting tags for novel: {}", novelId);
        
        List<Long> tagIds = novelTagRepository.findTagIdsByNovelId(novelId);
        List<Tag> tags = tagRepository.findAllById(tagIds);
        List<String> tagNames = tags.stream()
                .map(Tag::getTagName)
                .collect(Collectors.toList());
        
        TagResponseDTO.NovelTagsResponse response = new TagResponseDTO.NovelTagsResponse();
        response.setNovelId(novelId);
        response.setTagNames(tagNames);
        response.setTagCount(tagNames.size());
        
        return response;
    }
    
    // 태그명으로 작품 검색
    @Transactional(readOnly = true)
    public TagResponseDTO.TagSearchResponse searchNovelsByTag(String tagName) {
        log.info("Searching novels by tag: {}", tagName);
        List<Long> novelIds = novelTagRepository.findNovelIdsByTagNameContaining(tagName);
        
        TagResponseDTO.TagSearchResponse response = new TagResponseDTO.TagSearchResponse();
        response.setNovelIds(novelIds);
        response.setResultCount(novelIds.size());
        
        return response;
    }
    
    // 태그명으로 작품 검색 (소설 상세 정보 포함)
    @Transactional(readOnly = true)
    public List<ResponseGetNovelDto> searchNovelsByTagWithDetails(String tagName) {
        log.info("Searching novels by tag with details: {}", tagName);
        List<Long> novelIds = novelTagRepository.findNovelIdsByTagNameContaining(tagName);
        
        List<ResponseGetNovelDto> novels = new ArrayList<>();
        for (Long novelId : novelIds) {
            try {
                // NovelRepository를 통해 소설 정보 가져오기
                var novelEntity = novelRepository.findById(novelId).orElse(null);
                if (novelEntity != null) {
                    ResponseGetNovelDto novelDto = new ResponseGetNovelDto();
                    novelDto.setNovelId(novelEntity.getNovelId());
                    novelDto.setTitle(novelEntity.getTitle());
                    novelDto.setSummary(novelEntity.getSummary());
                    novelDto.setTitleImage(novelEntity.getTitleImage());
                    novelDto.setAvailability(Availability.valueOf(novelEntity.getAvailability().toString()));
                    novelDto.setAuthor(novelEntity.getUser().getUserName());
                    novelDto.setFinished(novelEntity.isFinished());
                    novelDto.setDeleted(novelEntity.isDeleted());
                    novelDto.setBanned(novelEntity.isBanned());
                    
                    // 태그 정보 추가
                    try {
                        var tagResponse = getTagsByNovelId(novelId);
                        novelDto.setTags(tagResponse.getTagNames());
                    } catch (Exception e) {
                        log.warn("태그 정보를 가져오는데 실패했습니다. novelId: {}, error: {}", novelId, e.getMessage());
                        novelDto.setTags(List.of());
                    }
                    
                    novels.add(novelDto);
                }
            } catch (Exception e) {
                log.warn("소설 정보를 가져오는데 실패했습니다. novelId: {}, error: {}", novelId, e.getMessage());
            }
        }
        
        return novels;
    }
    
    // 태그 삭제 (요청자 정보 포함)
    @Transactional
    public void deleteTag(Long novelId, String tagName, String requesterType, String requesterId) {
        log.info("Deleting tag: {} from novel: {} by {} (ID: {})", tagName, novelId, requesterType, requesterId);
        
        if (novelTagRepository.existsByNovelIdAndTagName(novelId, tagName)) {
            // novel_tags에서 연결 제거
            novelTagRepository.deleteByNovelIdAndTagName(novelId, tagName);
            log.info("Tag connection deleted: {} from novel {} by {} (ID: {})", tagName, novelId, requesterType, requesterId);
            
            // 기본 태그가 아닌 경우, 다른 소설에서 사용하지 않으면 tags 테이블에서도 삭제
            if (!isDefaultTag(tagName)) {
                // 해당 태그를 사용하는 다른 소설이 있는지 확인
                List<Long> novelsUsingTag = novelTagRepository.findNovelIdsByTagNameContaining(tagName);
                if (novelsUsingTag.isEmpty()) {
                    // 다른 소설에서 사용하지 않으면 tags 테이블에서 삭제
                    tagRepository.deleteByTagName(tagName);
                    log.info("Custom tag deleted from tags table: {} by {} (ID: {})", tagName, requesterType, requesterId);
                } else {
                    log.info("Custom tag still used by other novels: {}", tagName);
                }
            }
        } else {
            log.warn("Tag not found: {} for novel {}", tagName, novelId);
        }
    }
    
    // 태그 삭제 (기존 호환성을 위한 오버로드)
    @Transactional
    public void deleteTag(Long novelId, String tagName) {
        deleteTag(novelId, tagName, "UNKNOWN", "UNKNOWN");
    }
    
    // 작품의 모든 태그 삭제
    @Transactional
    public void deleteAllTagsByNovelId(Long novelId) {
        log.info("Deleting all tags for novel: {}", novelId);
        novelTagRepository.deleteByNovelId(novelId);
        log.info("All tags deleted for novel: {}", novelId);
    }
    
    // 태그명으로 태그 검색 (기본 태그들)
    @Transactional(readOnly = true)
    public List<Tag> searchTagsByTagName(String tagName) {
        log.info("Searching tags by name: {}", tagName);
        return tagRepository.findByTagNameContaining(tagName);
    }
    
    // 특정 작품의 특정 태그 존재 여부 확인
    @Transactional(readOnly = true)
    public boolean isTagExists(Long novelId, String tagName) {
        return novelTagRepository.existsByNovelIdAndTagName(novelId, tagName);
    }
    
    // 기본 태그 목록 조회
    @Transactional(readOnly = true)
    public TagResponseDTO.TagListResponse getCommonTags() {
        log.info("Getting common tags");
        List<Tag> tags = tagRepository.findAllByOrderByTagNameAsc();
        List<String> tagNames = tags.stream()
                .map(Tag::getTagName)
                .collect(Collectors.toList());
        
        TagResponseDTO.TagListResponse response = new TagResponseDTO.TagListResponse();
        response.setTagNames(tagNames);
        response.setTotalCount(tagNames.size());
        
        return response;
    }
    
    // 새 태그 생성
    public Tag createTag(String tagName) {
        log.info("Creating new tag: {}", tagName);
        
        if (tagRepository.existsByTagName(tagName)) {
            log.warn("Tag already exists: {}", tagName);
            return tagRepository.findByTagName(tagName).orElse(null);
        }
        
        Tag tag = new Tag();
        tag.setTagName(tagName);
        Tag savedTag = tagRepository.save(tag);
        log.info("New tag created: {}", tagName);
        
        return savedTag;
    }
    
    // 기본 태그 목록
    private static final List<String> DEFAULT_TAGS = Arrays.asList(
        "SF", "호러", "일상", "역사", "스포츠", "음악", "요리", "여행",
        "동물", "자연", "우주", "마법", "전쟁", "정치", "드라마", "로맨스",
        "스릴러", "액션", "코미디", "판타지"
    );
    
    // 기본 태그인지 확인
    public boolean isDefaultTag(String tagName) {
        return DEFAULT_TAGS.contains(tagName);
    }
    
    // 기본 태그 목록 반환
    public List<String> getDefaultTags() {
        return new ArrayList<>(DEFAULT_TAGS);
    }
    
    // 기본 태그만 반환
    @Transactional(readOnly = true)
    public TagResponseDTO.TagListResponse getDefaultTagsOnly() {
        List<String> defaultTags = getDefaultTags();
        
        TagResponseDTO.TagListResponse response = new TagResponseDTO.TagListResponse();
        response.setTagNames(defaultTags);
        response.setTotalCount(defaultTags.size());
        
        return response;
    }
    
    // 사용자 추가 태그만 반환
    @Transactional(readOnly = true)
    public TagResponseDTO.TagListResponse getUserAddedTagsOnly() {
        List<String> allTags = tagRepository.findAll().stream()
            .map(Tag::getTagName)
            .collect(Collectors.toList());
        
        List<String> userAddedTags = allTags.stream()
            .filter(tagName -> !isDefaultTag(tagName))
            .collect(Collectors.toList());
        
        TagResponseDTO.TagListResponse response = new TagResponseDTO.TagListResponse();
        response.setTagNames(userAddedTags);
        response.setTotalCount(userAddedTags.size());
        
        return response;
    }
} 