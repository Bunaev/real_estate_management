package com.search_service.entity;

import com.search_service.util.DisplayNameProvider;
import java.util.List;

public enum ApartmentType implements DisplayNameProvider {
    STUDIO("с", "студия"),
    ONE_ROOM("1кк", "1", "1к", "1к-квартира"),
    TWO_ROOM_EURO("2e", "2е"),
    TWO_ROOM("2кк", "2", "2к", "2к-квартира"),
    THREE_ROOM_EURO("3e", "3е"),
    THREE_ROOM("3кк", "3", "3к", "3к-квартира"),
    FOUR_ROOM_EURO("4e", "4е");

    private final List<String> args;

    ApartmentType(String... s1) {
        this.args = List.of(s1);
    }

    public static ApartmentType parse(String arg) {
        String normalized = arg.toLowerCase().trim();
        for (ApartmentType type : values()) {
            if (type.args.contains(normalized)) {
                return type;
            }
        }
        throw new RuntimeException("Неверный тип квартиры.");
    }

    @Override
    public String getDisplayName() {
        return switch (this) {
            case STUDIO -> "Студия";
            case ONE_ROOM -> "1к-квартира";
            case TWO_ROOM_EURO -> "2Е";
            case TWO_ROOM -> "2к-квартира";
            case THREE_ROOM_EURO -> "3Е";
            case THREE_ROOM -> "3к-квартира";
            case FOUR_ROOM_EURO -> "4Е";
        };
    }
}