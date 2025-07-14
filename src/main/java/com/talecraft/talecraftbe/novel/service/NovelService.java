package com.talecraft.talecraftbe.novel.service;


import com.talecraft.talecraftbe.novel.repository.NovelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NovelService {
    private final NovelRepository novelRepository;

    @Autowired
    public NovelService(NovelRepository novelRepository) {
        this.novelRepository = novelRepository;
    }



}
