package com.talecraft.talecraftbe.global.s3;

import com.talecraft.talecraftbe.global.s3.dto.request.ImageUrlDto;
import com.talecraft.talecraftbe.global.s3.dto.imageUrlDto;
import com.talecraft.talecraftbe.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/file")
public class S3Controller {
    private final S3Service s3Service;

    @Autowired
    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

/*
    @PostMapping("/image/upload")
    public ResponseEntity<Integer> imageUpload(@RequestBody ImageUrlDto image, @AuthenticationPrincipal User user) {
        s3Service.uploadFile(image);
    }*/
}
