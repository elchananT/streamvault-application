package com.streamsault.contentservice.services;

import com.streamsault.contentservice.domain.Content;
import com.streamsault.contentservice.domain.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {
    private final ContentRepository contentRepository;

    @Transactional(readOnly = true)
    public Optional<Content> findById(UUID id) {
        return contentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Content> listContent(int page, int size, String genre) {
        PageRequest pageRequest = PageRequest.of(page, size);
        if (genre != null && !genre.isBlank()) {
            return contentRepository.findByGenreIgnoreCase(genre, pageRequest).getContent();
        }

        return contentRepository.findAll(pageRequest).getContent();
    }

    @Transactional(readOnly = true)
    public List<Content> search(String query, int page, int size) {
        return contentRepository.findByTitleContainingIgnoreCase(query, PageRequest.of(page, size));
    }

    @Transactional
    public Content save(Content content) {
        return contentRepository.save(content);
    }
}
