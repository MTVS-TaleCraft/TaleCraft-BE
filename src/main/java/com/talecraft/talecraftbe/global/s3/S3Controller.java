package com.talecraft.talecraftbe.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/presigned-url")
    public ResponseEntity<ResponseImgDto> upload(@RequestBody RequestImgDto requestImgDto) {
        return s3Service.getPutSignedUrl(requestImgDto);
    }
}