package com.edugauge.global;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.edugauge.dto.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception
    ) {
        ErrorResponse response = new ErrorResponse(
            "DUPLICATE_USER", exception.getMessage()
        );

        return ResponseEntity
                .status((HttpStatus.CONFLICT))
                .body(response);
    }
}
