package com.streamsault.streamingservice.grpc;

import com.streamsault.streamingservice.dto.PresignedUrl;
import com.streamsault.streamingservice.services.S3Service;
import com.streamvault.proto.streaming.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class StreamingGrpcServiceImpl extends StreamingGrpcServiceGrpc.StreamingGrpcServiceImplBase {

    private final S3Service s3Service;

    @Override
    public void getStreamUrl(GetStreamUrlRequest request, StreamObserver<StreamUrlResponse> observer) {
        try {
            String s3Key = "videos/" + request.getContentId() + "/master.m3u8";
            PresignedUrl presignedUrl = s3Service.generatePresignedUrl(s3Key);

            observer.onNext(StreamUrlResponse.newBuilder()
                    .setPresignedUrl(presignedUrl.url())
                    .setContentId(request.getContentId())
                    .setExpiresAt(presignedUrl.expiresAt())
                    .build());
        } catch (Exception e) {
            log.error("Error getting stream url", e);
            observer.onNext(StreamUrlResponse.newBuilder().build());
        }

        observer.onCompleted();
    }

    @Override
    public void getUploadUrl(GetUploadUrlRequest request, StreamObserver<UploadUrlResponse> observer) {
        try {
            String s3Key = "videos/" + request.getContentId() + "/" + request.getFilename();
            PresignedUrl presignedUrl = s3Service.generateUploadUrl(s3Key, request.getContentType());

            observer.onNext(UploadUrlResponse.newBuilder()
                    .setPresignedUrl(presignedUrl.url())
                    .setS3Key(s3Key)
                    .build());
        } catch (Exception e) {
            log.error("Error getting upload url", e);
            observer.onNext(UploadUrlResponse.newBuilder().build());
        }

        observer.onCompleted();
    }
}
