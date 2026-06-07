package com.streamsault.apigateway.security;

import java.util.UUID;

public record AuthenticatedUser(UUID userId, String email) {
}
