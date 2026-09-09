package com.search_service.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class KeyboardLayoutConverter {

    private static final Map<Character, Character> EN_TO_RU = new LinkedHashMap<>();
    private static final Map<Character, Character> RU_TO_EN = new LinkedHashMap<>();

    static {
        putEnRu('a', 'ф');
        putEnRu('b', 'и');
        putEnRu('c', 'с');
        putEnRu('d', 'в');
        putEnRu('e', 'у');
        putEnRu('f', 'а');
        putEnRu('g', 'п');
        putEnRu('h', 'р');
        putEnRu('i', 'ш');
        putEnRu('j', 'о');
        putEnRu('k', 'л');
        putEnRu('l', 'д');
        putEnRu('m', 'ь');
        putEnRu('n', 'т');
        putEnRu('o', 'щ');
        putEnRu('p', 'з');
        putEnRu('q', 'й');
        putEnRu('r', 'к');
        putEnRu('s', 'ы');
        putEnRu('t', 'е');
        putEnRu('u', 'г');
        putEnRu('v', 'м');
        putEnRu('w', 'ц');
        putEnRu('x', 'ч');
        putEnRu('y', 'н');
        putEnRu('z', 'я');
        putEnRu('[', 'х');
        putEnRu(']', 'ъ');
        putEnRu(';', 'ж');
        putEnRu('\'', 'э');
        putEnRu(',', 'б');
        putEnRu('.', 'ю');
        putEnRu('`', 'ё');

        putRuEn('ф', 'a');
        putRuEn('и', 'b');
        putRuEn('с', 'c');
        putRuEn('в', 'd');
        putRuEn('у', 'e');
        putRuEn('а', 'f');
        putRuEn('п', 'g');
        putRuEn('р', 'h');
        putRuEn('ш', 'i');
        putRuEn('о', 'j');
        putRuEn('л', 'k');
        putRuEn('д', 'l');
        putRuEn('ь', 'm');
        putRuEn('т', 'n');
        putRuEn('щ', 'o');
        putRuEn('з', 'p');
        putRuEn('й', 'q');
        putRuEn('к', 'r');
        putRuEn('ы', 's');
        putRuEn('е', 't');
        putRuEn('г', 'u');
        putRuEn('м', 'v');
        putRuEn('ц', 'w');
        putRuEn('ч', 'x');
        putRuEn('н', 'y');
        putRuEn('я', 'z');
    }

    private KeyboardLayoutConverter() {
    }

    private static void putEnRu(char en, char ru) {
        EN_TO_RU.put(en, ru);
        RU_TO_EN.put(ru, en);
    }

    private static void putRuEn(char ru, char en) {
        RU_TO_EN.put(ru, en);
    }


    public static String toRussianLayout(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        StringBuilder result = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Character mapped = EN_TO_RU.get(Character.toLowerCase(c));
            if (mapped == null) {
                result.append(c);
            } else {
                result.append(Character.isUpperCase(c) ? Character.toUpperCase(mapped) : mapped);
            }
        }
        return result.toString();
    }


    public static String toEnglishLayout(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        StringBuilder result = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Character mapped = RU_TO_EN.get(Character.toLowerCase(c));
            if (mapped == null) {
                result.append(c);
            } else {
                result.append(Character.isUpperCase(c) ? Character.toUpperCase(mapped) : mapped);
            }
        }
        return result.toString();
    }

    public static List<String> getSearchVariants(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        Set<String> variants = new LinkedHashSet<>();
        variants.add(query);

        String russianLayout = toRussianLayout(query);
        if (!russianLayout.equals(query)) {
            variants.add(russianLayout);
        }

        String englishLayout = toEnglishLayout(query);
        if (!englishLayout.equals(query)) {
            variants.add(englishLayout);
        }

        return new ArrayList<>(variants);
    }
}
