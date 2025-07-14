package com.talecraft.talecraftbe.novel.model.entity;


import jakarta.persistence.*;

@Entity
@Table
public class NovelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private long novelId;

    @Column(nullable = false)
    private String title;

    @Column
    private String titleImage;

    @Column
    private String summary;

    @Column(nullable = false)
    private Availability availabilty;

    //유저 외래키 받아올것
    //@Column
    //private
}
