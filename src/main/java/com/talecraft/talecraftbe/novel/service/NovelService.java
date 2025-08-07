package com.talecraft.talecraftbe.novel.service;


import com.talecraft.talecraftbe.novel.dto.request.RequestPatchNovelDto;
import com.talecraft.talecraftbe.novel.dto.request.RequestPostNovelDto;
import com.talecraft.talecraftbe.novel.dto.response.*;
import com.talecraft.talecraftbe.novel.episode.repository.EpisodeRepository;
import com.talecraft.talecraftbe.novel.model.entity.Availability;
import com.talecraft.talecraftbe.novel.model.entity.NovelEntity;
import com.talecraft.talecraftbe.novel.repository.NovelRepository;
import com.talecraft.talecraftbe.tag.service.TagService;
import com.talecraft.talecraftbe.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NovelService {
    private final NovelRepository novelRepository;
    private final TagService tagService;
    private final EpisodeRepository episodeRepository;
    @Autowired
    public NovelService(NovelRepository novelRepository, TagService tagService, EpisodeRepository episodeRepository) {
        this.novelRepository = novelRepository;
        this.tagService = tagService;
        this.episodeRepository = episodeRepository;
    }

    @Transactional
    public ResponseEntity<ResponsePostNovelDto> createNovel(RequestPostNovelDto requestPostNovelDto, User user) {
        try {
        NovelEntity novelEntity = NovelEntity.builder()
                .title(requestPostNovelDto.getTitle())
                .titleImage(requestPostNovelDto.getTitleImage())
                .summary(requestPostNovelDto.getSummary())
                .availability(requestPostNovelDto.getAvailability())
                .user(user)
                .build();
        novelRepository.save(novelEntity);

        ResponsePostNovelDto responsePostNovelDto = new ResponsePostNovelDto();
        responsePostNovelDto.setNovelId(novelEntity.getNovelId());
        responsePostNovelDto.setMessage("Novel created");
            return new ResponseEntity<>(responsePostNovelDto, HttpStatus.CREATED);
        }
        catch (RuntimeException e){
            log.info(String.valueOf(e));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    //EPISODE까지 지우게해야함. (나중에 EPISODE구현완료시)
    //
    @Transactional
    public ResponseEntity<ResponseDeleteNovelDto> deleteNovel(long novelId,@AuthenticationPrincipal User user) {
        NovelEntity novelEntity = novelRepository.findByNovelId(novelId);
        novelEntity.updateIsBanned(true);
        novelRepository.save(novelEntity);
        ResponseDeleteNovelDto responseDeleteNovelDto = new ResponseDeleteNovelDto();
        return new ResponseEntity<>(responseDeleteNovelDto, HttpStatus.OK);
    }


    public ResponseEntity<ResponsePatchNovelDto> updateNovel(RequestPatchNovelDto requestPatchNovelDto, long novelId,@AuthenticationPrincipal User user) {
         NovelEntity novelEntity = novelRepository.findByNovelId(novelId);
         novelEntity.updateTitleImage(requestPatchNovelDto.getTitleImage());
         novelEntity.updateSummary(requestPatchNovelDto.getSummary());
         novelEntity.updateTitle(requestPatchNovelDto.getTitle());
         novelRepository.save(novelEntity);
         ResponsePatchNovelDto responsePatchNovelDto = new ResponsePatchNovelDto("success Update");
         return new ResponseEntity<>(responsePatchNovelDto, HttpStatus.OK);
    }

    //단건 조회
    @Transactional
    public ResponseEntity<ResponseGetNovelDto> getNovel(long novelId, @AuthenticationPrincipal User user) {
        try{
            NovelEntity novelEntity = novelRepository.findByNovelId(novelId);
            
            // 차단된 소설에 대한 접근 제어
            if (novelEntity.isBanned()) {
                // 관리자가 아닌 경우 차단된 소설 접근 불가
                if (user == null || user.getAuthorityId() == null || user.getAuthorityId() != 3L) {
                    log.warn("Non-admin user {} attempted to access banned novel {}", 
                            user != null ? user.getId() : "anonymous", novelId);
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
            
            //->Mapper 도입 고려
            ResponseGetNovelDto responseGetNovelDto = new ResponseGetNovelDto();
            responseGetNovelDto.setNovelId(novelEntity.getNovelId());
            setAuthor(novelEntity, responseGetNovelDto);
            responseGetNovelDto.setTitle(novelEntity.getTitle());
            responseGetNovelDto.setTitleImage(novelEntity.getTitleImage());
            responseGetNovelDto.setSummary(novelEntity.getSummary());
            responseGetNovelDto.setAvailability(novelEntity.getAvailability());
            responseGetNovelDto.setBanned(novelEntity.isBanned());
            
            // 태그 정보 추가
            try {
                var tagResponse = tagService.getTagsByNovelId(novelId);
                responseGetNovelDto.setTags(tagResponse.getTagNames());
            } catch (Exception e) {
                log.warn("태그 정보를 가져오는데 실패했습니다. novelId: {}, error: {}", novelId, e.getMessage());
                responseGetNovelDto.setTags(List.of());
            }
            
            log.info("getNovel - novelId: {}, isBanned: {}", novelId, novelEntity.isBanned());
            log.info("getNovel - responseGetNovelDto.isBanned: {}", responseGetNovelDto.isBanned());
            
            return new ResponseEntity<>(responseGetNovelDto, HttpStatus.OK);
            //
        }catch(RuntimeException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //내 글만 단건 조회
    @Transactional
    public ResponseEntity<ResponseGetNovelDto> getMyNovel(long novelId, @AuthenticationPrincipal User user) {
        try{
            NovelEntity novelEntity = novelRepository.findByNovelId(novelId);
            if(novelEntity.getUser().equals(user)) {
                //->Mapper 도입 고려
                ResponseGetNovelDto responseGetNovelDto = new ResponseGetNovelDto();
                responseGetNovelDto.setNovelId(novelEntity.getNovelId());
                responseGetNovelDto.setTitle(novelEntity.getTitle());
                setAuthor(novelEntity, responseGetNovelDto);
                responseGetNovelDto.setTitleImage(novelEntity.getTitleImage());
                responseGetNovelDto.setSummary(novelEntity.getSummary());
                responseGetNovelDto.setAvailability(novelEntity.getAvailability());
                responseGetNovelDto.setBanned(novelEntity.isBanned());
                // 태그 정보 추가
                try {
                    var tagResponse = tagService.getTagsByNovelId(novelId);
                    responseGetNovelDto.setTags(tagResponse.getTagNames());
                } catch (Exception e) {
                    log.warn("태그 정보를 가져오는데 실패했습니다. novelId: {}, error: {}", novelId, e.getMessage());
                    responseGetNovelDto.setTags(List.of());
                }
                
                return new ResponseEntity<>(responseGetNovelDto, HttpStatus.OK);
                //
            }else{
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }catch(RuntimeException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //리스트 조회
    //현재는 페이지네이션을 고려하지 않음(MVP)
    @Transactional
    public ResponseEntity<ResponseGetNovelListDto> getNovelList(String keyword, String type, User user) {
        try {
            List<NovelEntity> novelEntityList;

            // 검색 조건 분기
            if (type == null || keyword == null || keyword.isBlank()) {
                novelEntityList = novelRepository.findAllByAvailability(Availability.PUBLIC);
            } else {
                novelEntityList = switch (type) {
                    case "title" -> novelRepository.findAllByTitleAndAvailability(keyword,Availability.PUBLIC);
                    case "userName" -> novelRepository.findAllByUserUserNameAndAvailability(keyword,Availability.PUBLIC);
                    case "userId" -> novelRepository.findAllByUserIdAndAvailability(keyword,Availability.PUBLIC);
                    default -> throw new IllegalArgumentException("유효하지 않은 검색 타입입니다: " + type);
                };
            }

            // 관리자 권한 확인
            boolean isAdmin = user != null && user.getAuthorityId() != null && user.getAuthorityId() == 3L;
            log.info("getNovelList - user: {}, authorityId: {}, isAdmin: {}", 
                    user != null ? user.getId() : "null", 
                    user != null ? user.getAuthorityId() : "null", 
                    isAdmin);
            
            // 관리자가 아닌 경우 차단된 소설 필터링
            List<NovelEntity> filteredNovelList = novelEntityList;
            if (!isAdmin) {
                filteredNovelList = novelEntityList.stream()
                        .filter(novel -> !novel.isBanned())
                        .collect(Collectors.toList());
                log.info("Non-admin user - filtered novels from {} to {}", novelEntityList.size(), filteredNovelList.size());
            } else {
                log.info("Admin user - showing all novels: {}", novelEntityList.size());
            }

            List<ResponseGetNovelDto> responseGetNovelDtoList = filteredNovelList.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            ResponseGetNovelListDto response = new ResponseGetNovelListDto();
            response.setNovelList(responseGetNovelDtoList);
            response.setTotalElements(novelRepository.countByIsDeleted(false));
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            throw new RuntimeException("소설 검색 중 오류 발생", e);
        }

    }



    //내 작품만 조회
    @Transactional
    public ResponseEntity<ResponseGetNovelListDto> getMyNovelList(@AuthenticationPrincipal User user) {
        try{
            List<NovelEntity> novelEntityList = novelRepository.findAllByUser(user);
            ResponseGetNovelListDto responseGetNovelListDto = new ResponseGetNovelListDto();
            //가독성개선방향찾기
            List<ResponseGetNovelDto> responseGetNovelDtoList = new ArrayList<>();
            for (NovelEntity novelEntity : novelEntityList) {
                // 차단되지 않은 소설만 포함
                if (!novelEntity.isBanned()) {
                    ResponseGetNovelDto responseGetNovelDto = new ResponseGetNovelDto();
                    responseGetNovelDto.setNovelId(novelEntity.getNovelId());
                    setAuthor(novelEntity, responseGetNovelDto);
                    responseGetNovelDto.setTitle(novelEntity.getTitle());
                    responseGetNovelDto.setTitleImage(novelEntity.getTitleImage());
                    responseGetNovelDto.setSummary(novelEntity.getSummary());
                    responseGetNovelDto.setAvailability(novelEntity.getAvailability());
                    responseGetNovelDtoList.add(responseGetNovelDto);
                }
            }
            //에피소드 개수 세팅
            for(ResponseGetNovelDto responseGetNovelDto : responseGetNovelDtoList) {
                responseGetNovelDto.setEpisodeCount(episodeRepository.countByNovel_NovelIdAndIsDeleted(responseGetNovelDto.getNovelId(), false));
                log.info("Episode Count= {}",responseGetNovelDto.getEpisodeCount());
            }
            responseGetNovelListDto.setNovelList(responseGetNovelDtoList);
            return new ResponseEntity<>(responseGetNovelListDto,HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    private void setAuthor(NovelEntity novelEntity,ResponseGetNovelDto response) {
        if(novelEntity.getUser()!=null){
            response.setAuthor(novelEntity.getUser().getUserName());
        }else{
            log.info("user is null");
            response.setAuthor("No Author");
        }
    }

    private ResponseGetNovelDto convertToDto(NovelEntity novelEntity) {
        ResponseGetNovelDto responseGetNovelDto = new ResponseGetNovelDto();
        responseGetNovelDto.setNovelId(novelEntity.getNovelId());
        setAuthor(novelEntity, responseGetNovelDto);
        responseGetNovelDto.setTitle(novelEntity.getTitle());
        responseGetNovelDto.setTitleImage(novelEntity.getTitleImage());
        responseGetNovelDto.setSummary(novelEntity.getSummary());
        responseGetNovelDto.setAvailability(novelEntity.getAvailability());
        responseGetNovelDto.setBanned(novelEntity.isBanned());
        
        log.info("convertToDto - novelId: {}, title: {}, isBanned: {}", 
                novelEntity.getNovelId(), novelEntity.getTitle(), novelEntity.isBanned());
        
        // 태그 정보 추가
        try {
            var tagResponse = tagService.getTagsByNovelId(novelEntity.getNovelId());
            responseGetNovelDto.setTags(tagResponse.getTagNames());
        } catch (Exception e) {
            log.warn("태그 정보를 가져오는데 실패했습니다. novelId: {}, error: {}", novelEntity.getNovelId(), e.getMessage());
            responseGetNovelDto.setTags(List.of());
        }
        
        return responseGetNovelDto;
    }

    // 소설 차단/해제 (관리자용)
    @Transactional
    public ResponseEntity<?> toggleNovelBan(long novelId, User user) {
        try {
            log.info("toggleNovelBan 호출됨 - novelId: {}, user: {}", novelId, user != null ? user.getId() : "null");
            
            // 관리자 권한 확인
            if (user == null) {
                log.warn("사용자가 null입니다.");
                return ResponseEntity.status(403).body(Map.of("error", "관리자 권한이 필요합니다."));
            }
            
            if (user.getAuthorityId() == null) {
                log.warn("사용자 권한 ID가 null입니다. 사용자: {}", user.getId());
                return ResponseEntity.status(403).body(Map.of("error", "관리자 권한이 필요합니다."));
            }
            
            log.info("사용자 권한 ID: {}, 관리자 권한(3)과 비교: {}", user.getAuthorityId(), user.getAuthorityId() == 3L);
            
            if (user.getAuthorityId() != 3L) {
                log.warn("관리자 권한이 아닙니다. 사용자: {}, 권한: {}", user.getId(), user.getAuthorityId());
                return ResponseEntity.status(403).body(Map.of("error", "관리자 권한이 필요합니다."));
            }

            NovelEntity novelEntity = novelRepository.findByNovelId(novelId);
            if (novelEntity == null) {
                log.warn("소설을 찾을 수 없습니다. novelId: {}", novelId);
                return ResponseEntity.status(404).body(Map.of("error", "소설을 찾을 수 없습니다."));
            }

            // 현재 차단 상태를 반전
            boolean currentBanStatus = novelEntity.isBanned();
            boolean newBanStatus = !currentBanStatus;
            log.info("소설 차단 상태 변경 - novelId: {}, 현재: {}, 새로운: {}", novelId, currentBanStatus, newBanStatus);
            
            novelEntity.updateIsBanned(newBanStatus);
            novelRepository.save(novelEntity);
            
            // 저장 후 실제 데이터베이스에서 다시 조회하여 확인
            NovelEntity savedNovel = novelRepository.findByNovelId(novelId);
            log.info("데이터베이스 저장 후 실제 isBanned 값: {}", savedNovel.isBanned());

            String message = newBanStatus ? "소설이 차단되었습니다." : "소설 차단이 해제되었습니다.";
            log.info("소설 차단/해제 성공 - novelId: {}, 메시지: {}", novelId, message);
            
            return ResponseEntity.ok(Map.of(
                "message", message,
                "isBanned", newBanStatus,
                "novelId", novelId
            ));
        } catch (Exception e) {
            log.error("소설 차단/해제 중 오류 발생", e);
            return ResponseEntity.status(500).body(Map.of("error", "소설 차단/해제에 실패했습니다."));
        }
    }

    // 관리자용 특정 사용자의 모든 소설 목록 조회 (차단된 소설 포함)
    @Transactional
    public ResponseEntity<ResponseGetNovelListDto> getNovelListForAdmin(String userId, User adminUser) {
        try {
            log.info("getNovelListForAdmin 호출됨 - userId: {}, adminUser: {}", userId, adminUser != null ? adminUser.getId() : "null");
            
            // 관리자 권한 확인
            if (adminUser == null || adminUser.getAuthorityId() == null || adminUser.getAuthorityId() != 3L) {
                log.warn("Non-admin user {} attempted to access admin novel list", adminUser != null ? adminUser.getId() : "null");
                return ResponseEntity.status(403).build();
            }

            // 특정 사용자의 모든 소설 조회 (차단된 소설 포함)
            List<NovelEntity> novelEntityList = novelRepository.findAllByUserIdAndAvailability(userId, Availability.PUBLIC);
            log.info("Found {} novels for user {}", novelEntityList.size(), userId);

            // 모든 소설을 포함 (차단된 소설도 포함)
            List<ResponseGetNovelDto> responseGetNovelDtoList = novelEntityList.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            ResponseGetNovelListDto response = new ResponseGetNovelListDto();
            response.setNovelList(responseGetNovelDtoList);
            response.setTotalElements((long) responseGetNovelDtoList.size());
            
            log.info("Returning {} novels for admin", responseGetNovelDtoList.size());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("관리자용 소설 목록 조회 중 오류 발생", e);
            throw new RuntimeException("소설 검색 중 오류 발생", e);
        }
    }
}
