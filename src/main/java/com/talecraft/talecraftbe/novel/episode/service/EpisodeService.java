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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EpisodeService {

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
            if(novelEntity.getUser().getEmail().equals(user.getEmail())) {
                String note;
                if (requestPostEpisodeDto.getNote() == null) {
                    note = "";
                } else {
                    note = requestPostEpisodeDto.getNote();
                }
                EpisodeEntity episodeEntity = EpisodeEntity.builder().title(requestPostEpisodeDto.getTitle()).content(requestPostEpisodeDto.getContent()).note(note).build();
                episodeRepository.save(episodeEntity);
                ResponsePostEpisodeDto responsePostEpisodeDto = new ResponsePostEpisodeDto();
                responsePostEpisodeDto.setEpisodeId(episodeEntity.getEpisodesId());
                return new ResponseEntity<>(responsePostEpisodeDto, HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }




        } catch (RuntimeException e) {
            System.out.println(e);
            System.out.println("Error creating episode");
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
            return new ResponseEntity<>(new ResponseGetEpisodeDto(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ResponseGetEpisodeDto(), HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<ResponseUpdateEpisodeDto> updateEpisode(RequestUpdateEpisodeDto requestUpdateEpisodeDto, long episodeId) {
        try{
            EpisodeEntity episodeEntity = episodeRepository.findById(episodeId).orElseThrow(() -> new RuntimeException("No episode with id " + episodeId));
            episodeEntity.updateEpisode(requestUpdateEpisodeDto);
            episodeRepository.save(episodeEntity);
            return new ResponseEntity<>(new ResponseUpdateEpisodeDto(episodeEntity.getEpisodesId(),"update success"), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    public ResponseEntity<ResponseDeleteEpisodeDto> deleteEpisode(long episodeId) {
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
