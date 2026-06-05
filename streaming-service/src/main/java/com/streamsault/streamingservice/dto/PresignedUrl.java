package com.streamsault.streamingservice.dto;

public record PresignedUrl(String url, long expiresAt) {
}
