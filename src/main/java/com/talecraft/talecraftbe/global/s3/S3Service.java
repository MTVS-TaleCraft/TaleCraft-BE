package com.talecraft.talecraftbe.global.s3;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
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
    Duration duration = Duration.ofSeconds(30);


    public ResponseEntity<ResponseImgDto> getPutSignedUrl(String fileName) {
        //요청객체를 만들고
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType("image/png") // MIME type 지정
                    .build();

        //그객체를 presign요청 객체에 또담고
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(duration)
                    .putObjectRequest(objectRequest)
                    .build();

        // 3. Presigned URL 생성
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        // 4. 저장할 publicURL 응답에 담기
        // 잠재적인 문제점을 생각해보자
        String publicUrl = buildPublicUrl(s3Client,bucket,fileName);
        // 4. 응답 객체 생성
        ResponseImgDto response = new ResponseImgDto(presignedRequest.url().toString(),publicUrl);
        return ResponseEntity.ok(response);
    }

    // 공개 접근 가능한 URL생성 메서드 (만료 안 됨, 퍼블릭 버킷일 때)
    private String buildPublicUrl(S3Client s3Client, String bucket, String key) {
        S3Utilities utilities = s3Client.utilities();
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return utilities.getUrl(request).toString();
    }

}