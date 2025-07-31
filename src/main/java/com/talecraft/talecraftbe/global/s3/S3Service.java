package com.talecraft.talecraftbe.global.s3;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
public class S3Service {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Autowired
    public S3Service(S3Presigner s3Presigner, S3Client s3Client) {
        this.s3Presigner = s3Presigner;
        this.s3Client = s3Client;
    }

    @Value("${AWS_BUCKET}")
    private String bucket;

    @Value("${AWS_CLOUDFRONT}")
    private String cloudFront;
    Duration duration = Duration.ofSeconds(60);


    public ResponseEntity<ResponseImgDto> getPutSignedUrl(RequestImgDto requestImgDto) {
        //요청객체를 만들고
        log.info("Getting url for request: {}", requestImgDto);
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(requestImgDto.getFileName())
                    .build();

        //그객체를 presign요청 객체에 또담고
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(duration)
                    .putObjectRequest(objectRequest)
                    .build();

        // 3. Presigned URL 생성
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        log.info("Presigned URL: [{}]", presignedRequest.url().toString());
        log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());
        // 4. 저장할 publicURL 응답에 담기
        String publicUrl = cloudFront+requestImgDto.getFileName();
        log.info("Public URL: [{}]", publicUrl);
        // 4. 응답 객체 생성
        ResponseImgDto response = new ResponseImgDto(presignedRequest.url().toString(),publicUrl);
        return ResponseEntity.ok(response);
    }

    private String buildPublicUrl( String key) {

        return cloudFront+key;
    }

}