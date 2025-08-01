package com.talecraft.talecraftbe.novel.episode.service;


import com.talecraft.talecraftbe.novel.episode.dto.request.RequestUpdateEpisodeDto;
import com.talecraft.talecraftbe.novel.episode.dto.request.RequestPostEpisodeDto;
import com.talecraft.talecraftbe.novel.episode.dto.response.*;
import com.talecraft.talecraftbe.novel.episode.model.entity.EpisodeEntity;
import com.talecraft.talecraftbe.novel.episode.repository.EpisodeRepository;
import com.talecraft.talecraftbe.novel.model.entity.NovelEntity;
import com.talecraft.talecraftbe.novel.repository.NovelRepository;
import com.talecraft.talecraftbe.user.entity.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EpisodeService {

    private static final Logger log = LoggerFactory.getLogger(EpisodeService.class);
    private final EpisodeRepository episodeRepository;
    private final NovelRepository novelRepository;

    @Autowired
    public EpisodeService(EpisodeRepository episodeRepository, NovelRepository novelRepository) {
        this.episodeRepository = episodeRepository;
        this.novelRepository = novelRepository;
    }

    @Transactional
    public ResponseEntity<ResponsePostEpisodeDto> createEpisode(long novelId, RequestPostEpisodeDto requestPostEpisodeDto, User user) {
        try {
            NovelEntity novelEntity = novelRepository.findById(novelId).orElseThrow(() -> new RuntimeException("No episode with id " + novelId));
            log.info("novelEntity Id ={}" ,novelId);
            log.info("novel user Id ={}" ,novelEntity.getUser().getId());
            log.info("novel user Id ={}" ,novelEntity.getUser().getId());
            if(novelEntity.getUser().getId().equals(user.getId())) {
                String note;
                if (requestPostEpisodeDto.isNotice()) {
                    note = requestPostEpisodeDto.getNote();
                } else {
                    note = "";
                }
                EpisodeEntity episodeEntity = EpisodeEntity.builder()
                        .title(requestPostEpisodeDto.getTitle())
                        .content(requestPostEpisodeDto.getContent())
                        .note(note)
                        .isNotice(requestPostEpisodeDto.isNotice())
                        .novel(novelEntity)
                        .isDeleted(false)
                        .build();
                episodeRepository.save(episodeEntity);
                ResponsePostEpisodeDto responsePostEpisodeDto = new ResponsePostEpisodeDto();
                responsePostEpisodeDto.setEpisodeId(episodeEntity.getEpisodesId());
                return new ResponseEntity<>(responsePostEpisodeDto, HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }




        } catch (RuntimeException e) {
            log.error("e: ", e);
            log.info("Error creating episode");
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<ResponseGetEpisodeDto> getEpisode(long episodeId,long novelId) {
        try{
            EpisodeEntity episodeEntity = episodeRepository.findById(episodeId).orElseThrow(() -> new RuntimeException("No episode with id " + episodeId));
            ResponseGetEpisodeDto responseGetEpisodeDto = new ResponseGetEpisodeDto();
            responseGetEpisodeDto.setEpisodeId(episodeEntity.getEpisodesId());
            responseGetEpisodeDto.setTitle(episodeEntity.getTitle());
            responseGetEpisodeDto.setContent(episodeEntity.getContent());
            responseGetEpisodeDto.setNote(episodeEntity.getNote());
            responseGetEpisodeDto.setCreateDate(episodeEntity.getCreatedDate());
            responseGetEpisodeDto.setNovelId(novelId);
            return new ResponseEntity<>(responseGetEpisodeDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ResponseGetEpisodeDto(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Transactional
    public ResponseEntity<ResponseGetEpisodeListDto> getEpisodeListByNovelId(long novelId) {
        try{
            log.info("Getting episode list for novel ID: {}", novelId);
            List<EpisodeEntity> episodeEntities = episodeRepository.findByNovelNovelIdAndIsDeletedFalse(novelId);
            log.info("Found {} episodes for novel ID: {}", episodeEntities.size(), novelId);
            
            List<ResponseGetEpisodeItemsDto> episodeItemsDtos = episodeEntities.stream()
                    .map(ResponseGetEpisodeItemsDto::new)
                    .toList();
            log.info("Converted to {} DTOs", episodeItemsDtos.size());
            
            ResponseGetEpisodeListDto responseGetEpisodeListDto = new ResponseGetEpisodeListDto();
            responseGetEpisodeListDto.setEpisodesList(episodeItemsDtos);
            log.info("Response DTO created with episodes list: {}", responseGetEpisodeListDto.getEpisodesList());
            
            return new ResponseEntity<>(responseGetEpisodeListDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error getting episode list for novel {}: ", novelId, e);
            return new ResponseEntity<>(new ResponseGetEpisodeListDto(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<ResponseGetEpisodeListDto> getEpisodeList() {
        try{
            List<EpisodeEntity> episodeEntities = episodeRepository.findAll();
            List<ResponseGetEpisodeItemsDto> episodeItemsDtos = episodeEntities.stream()
                    .map(ResponseGetEpisodeItemsDto::new)
                    .toList();
            return new ResponseEntity<>(new ResponseGetEpisodeListDto(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ResponseGetEpisodeListDto(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<ResponseUpdateEpisodeDto> updateEpisode(RequestUpdateEpisodeDto requestUpdateEpisodeDto, long episodeId , @AuthenticationPrincipal User user) {
        try{
            EpisodeEntity episodeEntity = episodeRepository.findById(episodeId).orElseThrow(() -> new RuntimeException("No episode with id " + episodeId));
            episodeEntity.updateEpisode(requestUpdateEpisodeDto);
            episodeRepository.save(episodeEntity);
            return new ResponseEntity<>(new ResponseUpdateEpisodeDto(episodeEntity.getEpisodesId(),"update success"), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Transactional
    public ResponseEntity<ResponseDeleteEpisodeDto> deleteEpisode(long episodeId,@AuthenticationPrincipal User user) {
        try{
            EpisodeEntity episodeEntity = episodeRepository.findById(episodeId).orElseThrow(() -> new RuntimeException("No episode with id " + episodeId));
            episodeEntity.updateDeleted();
            episodeRepository.save(episodeEntity);
            return new ResponseEntity<>(new ResponseDeleteEpisodeDto("Deleted Success"), HttpStatus.OK);
        }catch (RuntimeException e) {
            return new ResponseEntity<>(new ResponseDeleteEpisodeDto("Deleted Fail"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
