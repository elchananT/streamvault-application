package com.streamsault.apigateway.dto;

public record ContentGraphqlResponse(
        String id,
        String title,
        String description,
        String genre,
        String type,
        float rating,
        int releaseYear,
        String thumbnailUrl
) {
}
