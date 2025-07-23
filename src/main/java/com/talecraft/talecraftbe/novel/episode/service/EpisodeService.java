package com.talecraft.talecraftbe.novel.episode.service;


import com.talecraft.talecraftbe.novel.episode.dto.request.RequestPostEpisodeDto;
import com.talecraft.talecraftbe.novel.episode.dto.response.ResponsePostEpisodeDto;
import com.talecraft.talecraftbe.novel.episode.model.entity.EpisodeEntity;
import com.talecraft.talecraftbe.novel.episode.repository.EpisodeRepository;
import com.talecraft.talecraftbe.novel.model.entity.NovelEntity;
import com.talecraft.talecraftbe.novel.repository.NovelRepository;
import jakarta.transaction.Transactional;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

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
    public ResponseEntity<ResponsePostEpisodeDto> createEpisode(long novelId, RequestPostEpisodeDto requestPostEpisodeDto) {
        ResponsePostEpisodeDto responsePostEpisodeDto = null;
        try {
            NovelEntity novelEntity = novelRepository.findById(novelId).orElseThrow(() -> new RuntimeException("No episode with id " + novelId));
            String note;
            if (requestPostEpisodeDto.getNote() == null) {
                note = "";
            } else {
                note = requestPostEpisodeDto.getNote();
            }
            //공지여부가 뭔지 모르겠어서 일단 추가 안함.
            EpisodeEntity episodeEntity = new EpisodeEntity(requestPostEpisodeDto.getTitle(), requestPostEpisodeDto.getContent(), note);
            responsePostEpisodeDto = new ResponsePostEpisodeDto();


            episodeRepository.save(episodeEntity);
            responsePostEpisodeDto.setEpisodeId(episodeEntity.getEpisodesId());
            return new ResponseEntity<>(responsePostEpisodeDto, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.out.println(e);
            System.out.println("Error creating episode");
            return new ResponseEntity<>(responsePostEpisodeDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
