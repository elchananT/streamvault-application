package com.streamsault.streamingservice.services;

import com.streamsault.streamingservice.dto.PresignedUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.presign.expiry-minutes}")
    private int expiryMinutes;

    public PresignedUrl generatePresignedUrl(String s3Key) {
        GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expiryMinutes))
                .getObjectRequest(r -> r.bucket(bucket).key(s3Key))
                .build();

        PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(request);
        long expiresAt = Instant.now().plusSeconds(expiryMinutes).toEpochMilli();

        log.info("Generated presigned url for s3 key {}", s3Key);

        return new PresignedUrl(presigned.url().toString(), expiresAt);
    }

    public PresignedUrl generateUploadUrl(String s3Key, String contentType) {
        PutObjectPresignRequest request = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(r -> r.bucket(bucket).key(s3Key).contentType(contentType))
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(request);
        long expiresAt = Instant.now().plusSeconds(15 * 60L).toEpochMilli();

        return new PresignedUrl(presigned.url().toString(), expiresAt);
    }
}
