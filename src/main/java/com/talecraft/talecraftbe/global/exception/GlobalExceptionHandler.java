package com.talecraft.talecraftbe.global.exception;

import com.talecraft.talecraftbe.comment.exception.NovelNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NovelNotFoundException.class)
    public ResponseEntity<String> handleNovelNotFound(NovelNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
// 필요시 다른 예외도 추가 가능
