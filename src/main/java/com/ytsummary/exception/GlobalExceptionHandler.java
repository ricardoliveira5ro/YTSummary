package com.ytsummary.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @ExceptionHandler(TranscriptNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTranscriptNotFoundException(TranscriptNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("No transcripts", showDetails(ex.getMessage())));
    }

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrlException(InvalidUrlException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Invalid Youtube URL", showDetails(ex.getMessage())));
    }

    @ExceptionHandler(YoutubeException.class)
    public ResponseEntity<ErrorResponse> handleYoutubeException(YoutubeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Youtube internal error", showDetails(ex.getMessage())));
    }

    @ExceptionHandler(OpenAIException.class)
    public ResponseEntity<ErrorResponse> handleOpenAIException(OpenAIException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("OpenAI internal error", showDetails(ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred", showDetails(ex.getMessage())));
    }

    private String showDetails(String details) {
        return "dev".equalsIgnoreCase(activeProfile) ? details : null;
    }
}
