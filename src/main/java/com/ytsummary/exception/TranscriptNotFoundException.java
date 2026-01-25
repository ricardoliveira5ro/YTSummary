package com.ytsummary.exception;

public class TranscriptNotFoundException extends RuntimeException {
    public TranscriptNotFoundException(String message) {
        super(message);
    }
}
