package com.talecraft.talecraftbe.novel.service;


import com.talecraft.talecraftbe.novel.dto.request.RequestPatchNovelDto;
import com.talecraft.talecraftbe.novel.dto.request.RequestPostNovelDto;
import com.talecraft.talecraftbe.novel.dto.response.*;
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
public class NovelService {
    private final NovelRepository novelRepository;

    @Autowired
    public NovelService(NovelRepository novelRepository) {
        this.novelRepository = novelRepository;
    }

    @Transactional
    public ResponseEntity<ResponsePostNovelDto> createNovel(RequestPostNovelDto requestPostNovelDto, User user) {
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

        return new ResponseEntity<>(responsePostNovelDto, HttpStatus.CREATED);
    }

    //EPISODE까지 지우게해야함. (나중에 EPISODE구현완료시)
    //
    @Transactional
    public ResponseEntity<ResponseDeleteNovelDto> deleteNovel(long novelId, User user) {
        NovelEntity novelEntity = novelRepository.findByNovelId(novelId);
        novelEntity.updateIsBanned(true);
        novelRepository.save(novelEntity);
        ResponseDeleteNovelDto responseDeleteNovelDto = new ResponseDeleteNovelDto();
        return new ResponseEntity<>(responseDeleteNovelDto, HttpStatus.OK);
    }


    public ResponseEntity<ResponsePatchNovelDto> updateNovel(RequestPatchNovelDto requestPatchNovelDto, long novelId,User user) {
         NovelEntity novelEntity = novelRepository.findByNovelId(novelId);
         novelEntity.updateTitleImage(requestPatchNovelDto.getTitleImage());
         novelEntity.updateSummary(requestPatchNovelDto.getSummary());
         novelEntity.updateTitle(requestPatchNovelDto.getTitle());
         novelRepository.save(novelEntity);
         ResponsePatchNovelDto responsePatchNovelDto = new ResponsePatchNovelDto("success Update");
         return new ResponseEntity<>(responsePatchNovelDto, HttpStatus.OK);
    }


    @Transactional
    public ResponseEntity<ResponseGetNovelDto> getNovel(long novelId, User user) {
        NovelEntity novelEntity = novelRepository.findByNovelId(novelId);
        ResponseGetNovelDto responseGetNovelDto = new ResponseGetNovelDto();
        responseGetNovelDto.setNovelId(novelEntity.getNovelId());
        responseGetNovelDto.setTitle(novelEntity.getTitle());
        responseGetNovelDto.setTitleImage(novelEntity.getTitleImage());
        responseGetNovelDto.setSummary(novelEntity.getSummary());
        responseGetNovelDto.setAvailability(novelEntity.getAvailability());
        return new ResponseEntity<>(responseGetNovelDto, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<ResponseGetListNovelDto> getNovelList( User user) {
        List<NovelEntity> novelEntityList = novelRepository.findAll();
        ResponseGetListNovelDto responseGetListNovelDto = new ResponseGetListNovelDto();
        return new ResponseEntity<>(responseGetListNovelDto,HttpStatus.OK);
    }

}
