package com.search_service.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KeyboardLayoutConverterTest {

    @Test
    void shouldConvertLatinToRussianLayout() {
        assertThat(KeyboardLayoutConverter.toRussianLayout("vjcrjd")).isEqualTo("москов");
        assertThat(KeyboardLayoutConverter.toRussianLayout("VJCRJD")).isEqualTo("МОСКОВ");
    }

    @Test
    void shouldConvertRussianToEnglishLayout() {
        assertThat(KeyboardLayoutConverter.toEnglishLayout("москва")).isEqualTo("vjcrdf");
        assertThat(KeyboardLayoutConverter.toEnglishLayout("Москва")).isEqualTo("Vjcrdf");
    }

    @Test
    void shouldKeepNonConvertibleChars() {
        assertThat(KeyboardLayoutConverter.toRussianLayout("ЖК vjcrjd")).isEqualTo("ЖК москов");
        assertThat(KeyboardLayoutConverter.toEnglishLayout("ящщь")).isEqualTo("zoom");
    }

    @Test
    void shouldReturnSearchVariants() {
        List<String> variants = KeyboardLayoutConverter.getSearchVariants("vjcrjd");

        assertThat(variants).contains("vjcrjd", "москов");
    }

    @Test
    void shouldReturnOriginalForNullBlankAndPureSymbols() {
        assertThat(KeyboardLayoutConverter.getSearchVariants(null)).isEmpty();
        assertThat(KeyboardLayoutConverter.getSearchVariants("   ")).isEmpty();
        assertThat(KeyboardLayoutConverter.getSearchVariants("123")).containsExactly("123");
    }
}
