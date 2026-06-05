package com.streamsault.watchlistservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "watchlist_items", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "content_id" }))
public class WatchlistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "user_id")
    private String userId;

    @Column(nullable = false, name = "content_id")
    private String contentId;

    @Column(updatable = false, nullable = false)
    @Builder.Default
    private LocalDateTime addedAt = LocalDateTime.now();
}
