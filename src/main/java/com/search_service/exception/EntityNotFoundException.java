package com.search_service.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Long id) {
        super(entityName + " с ID " + id + " не найден(а)");
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}