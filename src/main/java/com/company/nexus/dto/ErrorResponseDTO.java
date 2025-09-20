package com.company.nexus.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        int statusCode,
        String message,
        LocalDateTime timestamp
) {
}
