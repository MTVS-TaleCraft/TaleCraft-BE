package com.talecraft.talecraftbe.comment.exception;

public class NovelNotFoundException extends RuntimeException {
    public NovelNotFoundException(Long novelId) {
        super("No such novel with id: " + novelId);
    }
}