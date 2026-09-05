package com.search_service.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApartmentTypeTest {

    @Test
    void parse_shouldReturnCorrectEnum() {
        assertThat(ApartmentType.parse("студия")).isEqualTo(ApartmentType.STUDIO);
        assertThat(ApartmentType.parse("1")).isEqualTo(ApartmentType.ONE_ROOM);
        assertThat(ApartmentType.parse("2e")).isEqualTo(ApartmentType.TWO_ROOM_EURO);
        assertThat(ApartmentType.parse("2")).isEqualTo(ApartmentType.TWO_ROOM);
        assertThat(ApartmentType.parse("3e")).isEqualTo(ApartmentType.THREE_ROOM_EURO);
        assertThat(ApartmentType.parse("3")).isEqualTo(ApartmentType.THREE_ROOM);
        assertThat(ApartmentType.parse("4e")).isEqualTo(ApartmentType.FOUR_ROOM_EURO);
    }

    @Test
    void getDisplayName_shouldReturnHumanReadable() {
        assertThat(ApartmentType.STUDIO.getDisplayName()).isEqualTo("Студия");
        assertThat(ApartmentType.ONE_ROOM.getDisplayName()).isEqualTo("1к-квартира");
    }

    @Test
    void parse_invalidInput_shouldThrow() {
        try {
            ApartmentType.parse("несуществующий_тип");
            assertThat(false).isTrue(); // shouldn't reach
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Неверный тип квартиры");
        }
    }
}

class BathroomTypeTest {

    @Test
    void parse_shouldReturnCorrectEnum() {
        assertThat(BathroomType.parse("совмещенный")).isEqualTo(BathroomType.COMBINED);
        assertThat(BathroomType.parse("comb")).isEqualTo(BathroomType.COMBINED);
        assertThat(BathroomType.parse("раздельный")).isEqualTo(BathroomType.SEPARATE);
        assertThat(BathroomType.parse("sep")).isEqualTo(BathroomType.SEPARATE);
    }

    @Test
    void parse_invalidOrEmpty_shouldDefaultToCombined() {
        assertThat(BathroomType.parse(null)).isEqualTo(BathroomType.COMBINED);
        assertThat(BathroomType.parse("")).isEqualTo(BathroomType.COMBINED);
        assertThat(BathroomType.parse("   ")).isEqualTo(BathroomType.COMBINED);
    }
}

class StatusTest {

    @Test
    void parse_shouldReturnCorrectEnum() {
        assertThat(Status.parse("свободна")).isEqualTo(Status.AVAILABLE);
        assertThat(Status.parse("забронирована")).isEqualTo(Status.RESERVED);
        assertThat(Status.parse("продана")).isEqualTo(Status.SOLD);
    }

    @Test
    void parse_unknown_shouldDefaultToAvailable() {
        assertThat(Status.parse("неизвестно")).isEqualTo(Status.AVAILABLE);
    }
}

class EntityEqualityTest {

    @Test
    void location_shouldEqualById() {
        Location loc1 = Location.builder().id(1L).name("СПб").build();
        Location loc2 = Location.builder().id(1L).name("СПб").build();
        assertThat(loc1).isEqualTo(loc2);
    }

    @Test
    void developer_shouldEqualById() {
        Developer d1 = Developer.builder().id(1L).name("Dev").build();
        Developer d2 = Developer.builder().id(1L).name("Dev").build();
        assertThat(d1).isEqualTo(d2);
    }

    @Test
    void district_shouldEqualById() {
        District d1 = District.builder().id(1L).name("Район").build();
        District d2 = District.builder().id(1L).name("Район").build();
        assertThat(d1).isEqualTo(d2);
    }

    @Test
    void apartment_shouldEqualById() {
        Apartment a1 = Apartment.builder().id(1L).number(10).build();
        Apartment a2 = Apartment.builder().id(1L).number(10).build();
        assertThat(a1).isEqualTo(a2);
    }
}