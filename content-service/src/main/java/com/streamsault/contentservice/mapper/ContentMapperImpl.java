package com.streamsault.contentservice.mapper;

import com.streamsault.contentservice.domain.Content;
import com.streamvault.proto.content.ContentResponse;
import org.springframework.stereotype.Service;

@Service
public class ContentMapperImpl implements ContentMapper {
    @Override
    public ContentResponse toResponse(Content content) {
        return ContentResponse.newBuilder()
                .setContentId(String.valueOf(content.getId()))
                .setTitle(content.getTitle())
                .setDescription(content.getDescription())
                .setGenre(content.getGenre())
                .setType(content.getType().name())
                .setRating(content.getRating())
                .setReleaseYear(content.getReleaseYear())
                .setThumbnailUrl(content.getThumbnailUrl())
                .setFound(true)
                .build();
    }
}
