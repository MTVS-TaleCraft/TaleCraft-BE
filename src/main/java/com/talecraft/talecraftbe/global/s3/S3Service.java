package com.talecraft.talecraftbe.global.s3;

import io.awspring.cloud.s3.S3Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@Slf4j
public class S3Service {

    private final S3Template s3Template;

    // 생성자 주입 (자동으로 @Autowired 처리됨)
    public S3Service(S3Template s3Template) {
        this.s3Template = s3Template;
    }

    public void uploadFile(InputStream inputStream, String bucket, String key) {
        s3Template.upload(bucket, key, inputStream);
        log.info("Uploaded file to bucket: {}, key: {}", bucket, key);
    }

}