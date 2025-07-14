package com.talecraft.talecraftbe.novel.chapter.service;


import com.talecraft.talecraftbe.novel.chapter.dto.ChapterRequestDto;
import com.talecraft.talecraftbe.novel.chapter.model.entity.ChapterEntity;
import com.talecraft.talecraftbe.novel.chapter.repository.ChapterRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChapterService {

    private ChapterRepository chapterRepository;

    @Autowired
    public ChapterService(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }


    @Transactional()
    public void createChapter(ChapterRequestDto chapterRequestDto){
        ChapterEntity chapter = new ChapterEntity(  chapterRequestDto.getContent(),
                                                    chapterRequestDto.getNote(),
                                                    chapterRequestDto.getTitle()
                                                 );
        chapterRepository.save(chapter);

    }

}
