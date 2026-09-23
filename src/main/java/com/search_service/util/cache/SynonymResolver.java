package com.search_service.util.cache;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Slf4j
public class SynonymResolver {

    private SynonymResolver() {
        throw new UnsupportedOperationException("Нельзя создать экземпляр утилитного класса: " + SynonymResolver.class.getSimpleName());
    }

    public static <T> boolean resolveClass(LocalCacheContext<T> context, String name) {
        String normalized = name.toLowerCase().trim();
        String lowerCase = context.getTargetClass().getSimpleName().toLowerCase();
        return context.getClassSynonyms().contains(normalized) || lowerCase.equals(normalized);
    }

    public static <T> String resolveField(LocalCacheContext<T> context, String cellValue) {
        if (cellValue == null) {
            return null;
        }
        String normalized = cellValue.trim().toLowerCase().replaceAll("\\s+", "");
        if (normalized.isBlank()) {
            return null;
        }
        Map<String, List<String>> fieldSynonyms = context.getFieldSynonyms();
        for (Map.Entry<String, List<String>> entry : fieldSynonyms.entrySet()) {
            if (entry.getValue().contains(normalized)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static <T> Field findFieldIgnoreCase(LocalCacheContext<T> context, String fieldName) {
        if (fieldName == null) {
            return null;
        }
        for (Field field : context.getTargetClass().getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase(fieldName)) {
                return field;
            }
        }
        return null;
    }
}