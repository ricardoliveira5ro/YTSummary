package com.ytsummary.exception;

import java.time.LocalDateTime;

public record ErrorResponse(String message, String details, LocalDateTime timestamp) {

    public ErrorResponse {
        if (timestamp == null) timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message, String details) {
        this(message, details, null);
    }
}