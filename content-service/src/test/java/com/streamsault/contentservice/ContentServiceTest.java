package com.streamsault.contentservice;

import com.streamsault.contentservice.domain.Content;
import com.streamsault.contentservice.domain.ContentRepository;
import com.streamsault.contentservice.domain.ContentType;
import com.streamsault.contentservice.services.ContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Content Service Tests")
public class ContentServiceTest {
    @Mock
    ContentRepository contentRepository;
    @InjectMocks
    ContentService contentService;

    private Content movie;
    private UUID movieId;

    @BeforeEach
    void setUp() {
        movieId = UUID.randomUUID();
        movie = Content.builder()
                .id(movieId)
                .title("Spiderman")
                .genre("super-hero")
                .type(ContentType.MOVIE)
                .rating((float) 9.7)
                .releaseYear(2026)
                .build();
    }

    @Test
    @DisplayName("findById -> 200 OK")
    void findById_200_OK() {
        when(contentRepository.findById(movieId)).thenReturn(Optional.of(movie));
        assertThat(contentService.findById(movieId)).isPresent().get().extracting(Content::getTitle).isEqualTo("Spiderman");
    }

    @Test
    @DisplayName("findById -> 404 not found")
    void findById_404_notFound() {
        when(contentRepository.findById(any())).thenReturn(Optional.empty());
        assertThat(contentService.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    @DisplayName("listContent -> return paginated list")
    void listContent_200_OK() {
        when(contentRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(movie)));
        List<Content> result = contentService.listContent(0, 10, null);
        assertThat(result).hasSize(1).first().extracting(Content::getTitle).isEqualTo("Spiderman");
    }

    @Test
    @DisplayName("search -> return matching content")
    void search_200_OK() {
        when(contentRepository.findByTitleContainingIgnoreCase(eq("Spiderman"), any())).thenReturn(List.of(movie));
        assertThat(contentService.search("Spiderman", 0, 10)).hasSize(1).first().extracting(Content::getTitle).isEqualTo("Spiderman");
    }


}