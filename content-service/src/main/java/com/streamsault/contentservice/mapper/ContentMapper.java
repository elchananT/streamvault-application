package com.streamsault.contentservice.mapper;

import com.streamsault.contentservice.domain.Content;
import com.streamvault.proto.content.ContentResponse;

public interface ContentMapper {
    ContentResponse toResponse(Content content);
}
