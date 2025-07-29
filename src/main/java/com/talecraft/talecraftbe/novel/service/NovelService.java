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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
            //더강력한로깅으로 바꿀것
            e.printStackTrace();
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
            //->Mapper 도입 고려
            ResponseGetNovelDto responseGetNovelDto = new ResponseGetNovelDto();
            responseGetNovelDto.setNovelId(novelEntity.getNovelId());
            responseGetNovelDto.setUserId(novelEntity.getUser().getId());
            responseGetNovelDto.setTitle(novelEntity.getTitle());
            responseGetNovelDto.setTitleImage(novelEntity.getTitleImage());
            responseGetNovelDto.setSummary(novelEntity.getSummary());
            responseGetNovelDto.setAvailability(novelEntity.getAvailability());
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
                responseGetNovelDto.setTitleImage(novelEntity.getTitleImage());
                responseGetNovelDto.setSummary(novelEntity.getSummary());
                responseGetNovelDto.setAvailability(novelEntity.getAvailability());
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
    public ResponseEntity<ResponseGetNovelListDto> getNovelList() {
        try{
            List<NovelEntity> novelEntityList = novelRepository.findAll();
            ResponseGetNovelListDto responseGetNovelListDto = new ResponseGetNovelListDto();
            //가독성개선방향찾기
            List<ResponseGetNovelDto> responseGetNovelDtoList = new ArrayList<>();
            for (NovelEntity novelEntity : novelEntityList) {
                ResponseGetNovelDto responseGetNovelDto = new ResponseGetNovelDto();
                responseGetNovelDto.setNovelId(novelEntity.getNovelId());
                responseGetNovelDto.setUserId(novelEntity.getUser().getId());
                responseGetNovelDto.setTitle(novelEntity.getTitle());
                responseGetNovelDto.setTitleImage(novelEntity.getTitleImage());
                responseGetNovelDto.setSummary(novelEntity.getSummary());
                responseGetNovelDto.setAvailability(novelEntity.getAvailability());
                responseGetNovelDtoList.add(responseGetNovelDto);
            }
            responseGetNovelListDto.setNovelList(responseGetNovelDtoList);

            return new ResponseEntity<>(responseGetNovelListDto,HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
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
                ResponseGetNovelDto responseGetNovelDto = new ResponseGetNovelDto();
                responseGetNovelDto.setNovelId(novelEntity.getNovelId());
                responseGetNovelDto.setUserId(novelEntity.getUser().getId());
                responseGetNovelDto.setTitle(novelEntity.getTitle());
                responseGetNovelDto.setTitleImage(novelEntity.getTitleImage());
                responseGetNovelDto.setSummary(novelEntity.getSummary());
                responseGetNovelDto.setAvailability(novelEntity.getAvailability());
                responseGetNovelDtoList.add(responseGetNovelDto);
            }
            responseGetNovelListDto.setNovelList(responseGetNovelDtoList);

            return new ResponseEntity<>(responseGetNovelListDto,HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }
}
