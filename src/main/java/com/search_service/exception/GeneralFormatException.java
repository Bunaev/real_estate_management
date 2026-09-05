package com.search_service.exception;

import java.time.LocalDateTime;

public class GeneralFormatException extends RuntimeException {
    private final TypeError typeError;
    private final LocalDateTime timestamp;

    public GeneralFormatException(TypeError typeError) {
        super(typeError.getMessage());
        this.typeError = typeError;
        this.timestamp = LocalDateTime.now();
    }

    public GeneralFormatException(TypeError typeError, String message) {
        super(typeError.getMessage() + message);
        this.typeError = typeError;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        String red = "\u001B[31m";
        String reset = "\u001B[0m";
        return String.format(
                "%s[%s]%s %s | %s (at %s)",
                red, typeError.name(), reset,
                typeError.getMessage(),
                super.getMessage(),
                timestamp
        );
    }
}