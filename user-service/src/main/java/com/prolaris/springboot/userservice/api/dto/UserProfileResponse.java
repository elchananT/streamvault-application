package com.prolaris.springboot.userservice.api.dto;

import java.util.UUID;

public record UserProfileResponse(UUID id, String username, String email) {
}
