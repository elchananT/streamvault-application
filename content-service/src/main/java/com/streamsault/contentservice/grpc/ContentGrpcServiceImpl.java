package com.streamsault.contentservice.grpc;

import com.streamsault.contentservice.domain.Content;
import com.streamsault.contentservice.mapper.ContentMapper;
import com.streamsault.contentservice.services.ContentService;
import com.streamvault.proto.content.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class ContentGrpcServiceImpl extends ContentGrpcServiceGrpc.ContentGrpcServiceImplBase {
    private final ContentService contentService;
    private final ContentMapper contentMapper;

    @Override
    public void getContentById(GetContentByIdRequest request, StreamObserver<ContentResponse> observer) {
        try {
            UUID id = UUID.fromString(request.getContentId());
            contentService.findById(id).ifPresentOrElse(
                    content -> observer.onNext(contentMapper.toResponse(content)),
                    () -> observer.onNext(ContentResponse.newBuilder().setFound(false).build())
            );
        } catch (Exception e) {
            observer.onNext(ContentResponse.newBuilder().setFound(false).build());
        }

        observer.onCompleted();
    }

    @Override
    public void listContent(ListContentRequest request, StreamObserver<ListContentResponse>  observer) {
        List<Content> list = contentService.listContent(request.getPage(), request.getSize(), request.getGenre());

        ListContentResponse response = ListContentResponse.newBuilder()
                .addAllContent(list.stream().map(contentMapper::toResponse).toList())
                .setTotal(list.size())
                .build();
        observer.onNext(response);
        observer.onCompleted();
    }

    @Override
    public void searchContent(SearchContentRequest request, StreamObserver<ListContentResponse>  observer) {
        List<Content> results = contentService.search(request.getQuery(), request.getPage(), request.getSize());

        ListContentResponse response = ListContentResponse.newBuilder()
                .addAllContent(results.stream().map(contentMapper::toResponse).toList())
                .setTotal(results.size())
                .build();

        observer.onNext(response);
        observer.onCompleted();
    }
}
