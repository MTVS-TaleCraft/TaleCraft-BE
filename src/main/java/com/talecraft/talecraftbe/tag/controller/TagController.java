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
    public ResponseEntity<?> addTags(@PathVariable Long novelId, @RequestBody TagRequestDTO requestDTO) {
        log.info("Adding tags to novel {}: {}", novelId, requestDTO.getTagNames());
        
        try {
            tagService.addTags(novelId, requestDTO.getTagNames());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "태그가 성공적으로 추가되었습니다.");
            response.put("novelId", novelId);
            response.put("addedTags", requestDTO.getTagNames());
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error adding tags to novel {}: {}", novelId, e.getMessage());
            return ResponseEntity.badRequest().body("태그 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 작품의 태그 목록 조회
    @GetMapping("/novels/{novelId}")
    public ResponseEntity<?> getTagsByNovelId(@PathVariable Long novelId) {
        log.info("Getting tags for novel: {}", novelId);
        
        try {
            TagResponseDTO responseDTO = tagService.getTagsByNovelId(novelId);
            return ResponseEntity.ok().body(responseDTO);
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
            List<Long> novelIds = tagService.searchNovelsByTag(tagName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("tagName", tagName);
            response.put("novelIds", novelIds);
            response.put("count", novelIds.size());
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error searching novels by tag {}: {}", tagName, e.getMessage());
            return ResponseEntity.badRequest().body("태그 검색 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 특정 태그 삭제
    @DeleteMapping("/novels/{novelId}")
    public ResponseEntity<?> deleteTag(@PathVariable Long novelId, @RequestParam String tagName) {
        log.info("Deleting tag: {} from novel: {}", tagName, novelId);
        
        try {
            tagService.deleteTag(novelId, tagName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "태그가 성공적으로 삭제되었습니다.");
            response.put("novelId", novelId);
            response.put("deletedTag", tagName);
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error deleting tag {} from novel {}: {}", tagName, novelId, e.getMessage());
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
    
    // 태그명으로 태그 검색
    @GetMapping("/search/tags")
    public ResponseEntity<?> searchTagsByTagName(@RequestParam String tagName) {
        log.info("Searching tags by name: {}", tagName);
        
        try {
            List<com.talecraft.talecraftbe.tag.model.entity.Tag> tags = tagService.searchTagsByTagName(tagName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("searchTerm", tagName);
            response.put("tags", tags);
            response.put("count", tags.size());
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error searching tags by name {}: {}", tagName, e.getMessage());
            return ResponseEntity.badRequest().body("태그 검색 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 태그 존재 여부 확인
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
            log.error("Error checking tag existence for novel {} and tag {}: {}", novelId, tagName, e.getMessage());
            return ResponseEntity.badRequest().body("태그 확인 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
} 