package com.search_service.entity;

import com.search_service.util.DisplayNameProvider;
import java.util.regex.Pattern;

public enum BathroomType implements DisplayNameProvider {
    COMBINED(Pattern.compile("(?i)(совмещенный|сов|с|combined|comb)")),
    SEPARATE(Pattern.compile("(?i)(раздельный|разд|р|separate|sep)"));

    private final Pattern pattern;

    BathroomType(Pattern pattern) {
        this.pattern = pattern;
    }

    public static BathroomType parse(String input) {
        if (input != null && !input.isBlank()) {
            String trimmed = input.trim();
            for (BathroomType type : values()) {
                if (type.pattern.matcher(trimmed).matches()) {
                    return type;
                }
            }
            return COMBINED;
        } else {
            return COMBINED;
        }
    }

    public String getDisplayName() {
        return this == COMBINED ? "Совмещённый" : "Раздельный";
    }
}