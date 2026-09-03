package com.search_service.model.entityes;

import com.search_service.model.utils.DisplayNameProvider;
import java.util.List;

public enum Status implements DisplayNameProvider {
    AVAILABLE("свободна", "не забронирована", "с", "активна"),
    RESERVED("забронирована", "занята", "зарезервирована"),
    SOLD("продана", "не активна");

    private final List<String> synonyms;

    private Status(String... synonyms) {
        this.synonyms = List.of(synonyms);
    }

    public static Status parse(String input) {
        String normalized = input.toLowerCase().trim();

        for(Status status : values()) {
            if (status.synonyms.contains(normalized)) {
                return status;
            }
        }

        return AVAILABLE;
    }

    public String getDisplayName() {
        return switch (this) {
            case AVAILABLE -> "Свободна";
            case RESERVED -> "Забронирована";
            default -> "Продана";
        };
    }
}

