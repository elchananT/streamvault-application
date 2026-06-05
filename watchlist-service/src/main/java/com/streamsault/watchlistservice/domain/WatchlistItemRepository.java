package com.streamsault.watchlistservice.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, UUID> {
    List<WatchlistItem> findByUserId(String userId);
    boolean existsByUserIdAndByContentId(String userId,  String contentId);
    void deleteByUserIdAndContentId(String userId, String contentId);
}
