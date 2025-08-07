package com.talecraft.talecraftbe.tag.controller;

import com.talecraft.talecraftbe.tag.dto.request.TagRequestDTO;
import com.talecraft.talecraftbe.tag.dto.response.TagResponseDTO;
import com.talecraft.talecraftbe.tag.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/tags")
public class TagController {
    
    private final TagService tagService;
    
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }
    
    // 작품에 태그 추가
    @PostMapping("/novels/{novelId}")
    public ResponseEntity<?> addTags(@PathVariable Long novelId, 
                                    @RequestBody TagRequestDTO requestDTO,
                                    @RequestParam(required = false) String requesterType,
                                    @RequestParam(required = false) String requesterId) {
        log.info("Adding tags to novel {}: {} by {} (ID: {})", novelId, requestDTO.getTagNames(), requesterType, requesterId);
        
        try {
            tagService.addTags(novelId, requestDTO.getTagNames(), requesterType, requesterId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "태그가 성공적으로 추가되었습니다.");
            response.put("novelId", novelId);
            response.put("addedTags", requestDTO.getTagNames());
            response.put("requesterType", requesterType);
            response.put("requesterId", requesterId);
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error adding tags to novel {} by {} (ID: {}): {}", novelId, requesterType, requesterId, e.getMessage());
            return ResponseEntity.badRequest().body("태그 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 작품의 태그 목록 조회
    @GetMapping("/novels/{novelId}")
    public ResponseEntity<?> getTagsByNovelId(@PathVariable Long novelId) {
        log.info("Getting tags for novel: {}", novelId);
        
        try {
            TagResponseDTO.NovelTagsResponse response = tagService.getTagsByNovelId(novelId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error getting tags for novel {}: {}", novelId, e.getMessage());
            return ResponseEntity.badRequest().body("태그 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 태그명으로 작품 검색
    @GetMapping("/search")
    public ResponseEntity<?> searchNovelsByTag(@RequestParam String tagName) {
        log.info("Searching novels by tag: {}", tagName);
        
        try {
            TagResponseDTO.TagSearchResponse response = tagService.searchNovelsByTag(tagName);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error searching novels by tag {}: {}", tagName, e.getMessage());
            return ResponseEntity.badRequest().body("태그 검색 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 태그명으로 작품 검색 (소설 상세 정보 포함)
    @GetMapping("/search/novels")
    public ResponseEntity<?> searchNovelsByTagWithDetails(@RequestParam String tagName) {
        log.info("Searching novels by tag with details: {}", tagName);
        
        try {
            List<com.talecraft.talecraftbe.novel.dto.response.ResponseGetNovelDto> novels = tagService.searchNovelsByTagWithDetails(tagName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("tagName", tagName);
            response.put("novelList", novels);
            response.put("totalElements", novels.size());
            response.put("page", 0);
            response.put("totalPages", 1);
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error searching novels by tag {}: {}", tagName, e.getMessage());
            return ResponseEntity.badRequest().body("태그 검색 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 특정 태그 삭제
    @DeleteMapping("/novels/{novelId}")
    public ResponseEntity<?> deleteTag(@PathVariable Long novelId, 
                                      @RequestParam String tagName,
                                      @RequestParam(required = false) String requesterType,
                                      @RequestParam(required = false) String requesterId) {
        log.info("Deleting tag: {} from novel: {} by {} (ID: {})", tagName, novelId, requesterType, requesterId);
        
        try {
            tagService.deleteTag(novelId, tagName, requesterType, requesterId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "태그가 성공적으로 삭제되었습니다.");
            response.put("novelId", novelId);
            response.put("deletedTag", tagName);
            response.put("requesterType", requesterType);
            response.put("requesterId", requesterId);
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error deleting tag {} from novel {} by {} (ID: {}): {}", tagName, novelId, requesterType, requesterId, e.getMessage());
            return ResponseEntity.badRequest().body("태그 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 작품의 모든 태그 삭제
    @DeleteMapping("/novels/{novelId}/all")
    public ResponseEntity<?> deleteAllTagsByNovelId(@PathVariable Long novelId) {
        log.info("Deleting all tags for novel: {}", novelId);
        
        try {
            tagService.deleteAllTagsByNovelId(novelId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "모든 태그가 성공적으로 삭제되었습니다.");
            response.put("novelId", novelId);
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error deleting all tags for novel {}: {}", novelId, e.getMessage());
            return ResponseEntity.badRequest().body("태그 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 태그명으로 태그 검색 (기본 태그들)
    @GetMapping("/search/tags")
    public ResponseEntity<?> searchTagsByTagName(@RequestParam String tagName) {
        log.info("Searching tags by name: {}", tagName);
        
        try {
            List<com.talecraft.talecraftbe.tag.model.entity.Tag> tags = tagService.searchTagsByTagName(tagName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("tagName", tagName);
            response.put("tags", tags);
            response.put("count", tags.size());
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error searching tags by name {}: {}", tagName, e.getMessage());
            return ResponseEntity.badRequest().body("태그 검색 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 특정 작품의 특정 태그 존재 여부 확인
    @GetMapping("/novels/{novelId}/check")
    public ResponseEntity<?> checkTagExists(@PathVariable Long novelId, @RequestParam String tagName) {
        log.info("Checking if tag exists: {} for novel: {}", tagName, novelId);
        
        try {
            boolean exists = tagService.isTagExists(novelId, tagName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("novelId", novelId);
            response.put("tagName", tagName);
            response.put("exists", exists);
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error checking tag existence for novel {}: {}", novelId, e.getMessage());
            return ResponseEntity.badRequest().body("태그 존재 여부 확인 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 기본 태그 목록 조회
    @GetMapping("/common")
    public ResponseEntity<?> getCommonTags() {
        log.info("Getting common tags");
        
        try {
            TagResponseDTO.TagListResponse response = tagService.getCommonTags();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error getting common tags: {}", e.getMessage());
            return ResponseEntity.badRequest().body("기본 태그 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 새 태그 생성
    @PostMapping("/create")
    public ResponseEntity<?> createTag(@RequestParam String tagName) {
        log.info("Creating new tag: {}", tagName);
        
        try {
            com.talecraft.talecraftbe.tag.model.entity.Tag tag = tagService.createTag(tagName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "태그가 성공적으로 생성되었습니다.");
            response.put("tagId", tag.getTagId());
            response.put("tagName", tag.getTagName());
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error creating tag {}: {}", tagName, e.getMessage());
            return ResponseEntity.badRequest().body("태그 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 기본 태그만 반환
    @GetMapping("/default")
    public ResponseEntity<?> getDefaultTags() {
        log.info("Getting default tags");
        
        try {
            TagResponseDTO.TagListResponse response = tagService.getDefaultTagsOnly();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error getting default tags: {}", e.getMessage());
            return ResponseEntity.badRequest().body("기본 태그 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 사용자 추가 태그만 반환
    @GetMapping("/user-added")
    public ResponseEntity<?> getUserAddedTags() {
        log.info("Getting user added tags");
        
        try {
            TagResponseDTO.TagListResponse response = tagService.getUserAddedTagsOnly();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error getting user added tags: {}", e.getMessage());
            return ResponseEntity.badRequest().body("사용자 추가 태그 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 모든 태그를 기본/사용자 추가로 구분해서 반환
    @GetMapping("/all-categorized")
    public ResponseEntity<?> getAllTagsCategorized() {
        log.info("Getting all tags categorized");
        
        try {
            TagResponseDTO.TagListResponse defaultTags = tagService.getDefaultTagsOnly();
            TagResponseDTO.TagListResponse userAddedTags = tagService.getUserAddedTagsOnly();
            
            Map<String, Object> response = new HashMap<>();
            response.put("defaultTags", defaultTags.getTagNames());
            response.put("userAddedTags", userAddedTags.getTagNames());
            response.put("totalDefaultTags", defaultTags.getTotalCount());
            response.put("totalUserAddedTags", userAddedTags.getTotalCount());
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error getting categorized tags: {}", e.getMessage());
            return ResponseEntity.badRequest().body("태그 분류 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
} 