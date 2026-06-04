package com.streamsault.contentservice.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<Content, UUID> {
    Page<Content> findByGenreIgnoreCase(String genre, Pageable pageable);
    List<Content> findByTitleContainingIgnoreCase(String query,  Pageable pageable);
}
