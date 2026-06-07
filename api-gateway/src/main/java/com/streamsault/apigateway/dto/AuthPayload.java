package com.streamsault.apigateway.dto;

public record AuthPayload(String token, String username, String email) {
}
