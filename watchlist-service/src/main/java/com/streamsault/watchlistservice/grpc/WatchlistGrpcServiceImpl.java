package com.streamsault.watchlistservice.grpc;

import com.streamsault.watchlistservice.domain.WatchlistItem;
import com.streamsault.watchlistservice.domain.WatchlistItemRepository;
import com.streamvault.proto.watchlist.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class WatchlistGrpcServiceImpl extends WatchlistGrpcServiceGrpc.WatchlistGrpcServiceImplBase {

    private final WatchlistItemRepository watchlistItemRepository;

    @Override
    public void getWatchlist(GetWatchlistRequest request, StreamObserver<WatchlistResponse> observer) {
        List<WatchlistItem> items = watchlistItemRepository.findByUserId(request.getUserId());

        List<com.streamvault.proto.watchlist.WatchlistItem> protoItems = items.stream()
                .map(item -> com.streamvault.proto.watchlist.WatchlistItem.newBuilder()
                        .setContentId(item.getContentId())
                        .setTitle("")
                        .setAddedAt(item.getAddedAt().toEpochSecond(ZoneOffset.UTC) * 1000L)
                        .build())
                .toList();

        observer.onNext(WatchlistResponse.newBuilder()
                        .addAllItems(protoItems)
                        .setUserId(request.getUserId())
                        .build());

        observer.onCompleted();
    }

    @Override
    @Transactional
    public void addToWatchlist(WatchlistItemRequest request, StreamObserver<WatchlistActionResponse> observer) {
        if (!watchlistItemRepository.existsByUserIdAndByContentId(request.getUserId(), request.getContentId())) {
            watchlistItemRepository.save(WatchlistItem.builder()
                            .userId(request.getUserId())
                            .contentId(request.getContentId())
                            .build());
            observer.onNext(WatchlistActionResponse.newBuilder().setSuccess(true).setMessage("Added").build());
        } else {
            observer.onNext(WatchlistActionResponse.newBuilder().setSuccess(false).setMessage("Already in watchlist").build());
        }

        observer.onCompleted();
    }

    @Override
    @Transactional
    public void removeFromWatchlist(WatchlistItemRequest request, StreamObserver<WatchlistActionResponse> observer) {
        watchlistItemRepository.deleteByUserIdAndContentId(request.getUserId(), request.getContentId());
        observer.onNext(WatchlistActionResponse.newBuilder().setSuccess(true).setMessage("Removed").build());
        observer.onCompleted();
    }
}
