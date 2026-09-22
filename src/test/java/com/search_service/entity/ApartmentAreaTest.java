package com.search_service.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApartmentAreaTest {

    @Test
    void studio_shouldHaveNoKitchenAndRoomAsRemainder() {
        Apartment studio = Apartment.builder()
                .id(1L)
                .type(ApartmentType.STUDIO)
                .area(28.0)
                .build();

        studio.decomposeArea();

        assertThat(studio.getKitchenArea()).isNull();
        assertThat(studio.getHallwayArea()).isNotNull();
        assertThat(studio.getBathroomArea()).isNotNull();
        assertThat(studio.getRoomsArea()).isNotNull();
        assertThat(sum(studio)).isEqualTo(28.0);
    }

    @Test
    void euroApartment_shouldHaveKitchenAndRooms() {
        Apartment euro = Apartment.builder()
                .id(2L)
                .type(ApartmentType.TWO_ROOM_EURO)
                .area(45.0)
                .build();

        euro.decomposeArea();

        assertThat(euro.getKitchenArea()).isBetween(14.0, 21.0);
        assertThat(euro.getRoomsArea()).isPositive();
        assertThat(sum(euro)).isEqualTo(45.0);
    }

    @Test
    void regularApartment_shouldPreserveAreaOnDecompose() {
        Apartment regular = Apartment.builder()
                .id(3L)
                .type(ApartmentType.THREE_ROOM)
                .area(78.4)
                .build();

        regular.decomposeArea();

        assertThat(regular.getKitchenArea()).isNotNull();
        assertThat(regular.getRoomsArea()).isNotNull();
        assertThat(sum(regular)).isEqualTo(78.4);
    }

    @Test
    void areaShouldBeCalculatedFromComponentsWhenMissing() {
        Apartment apartment = Apartment.builder()
                .type(ApartmentType.ONE_ROOM)
                .kitchenArea(12.0)
                .hallwayArea(4.0)
                .bathroomArea(3.5)
                .roomsArea(18.0)
                .build();

        apartment.prePersistOrUpdate();

        assertThat(apartment.getArea()).isEqualTo(37.5);
    }

    private double sum(Apartment apartment) {
        return Math.round((nvl(apartment.getKitchenArea())
                + nvl(apartment.getHallwayArea())
                + nvl(apartment.getBathroomArea())
                + nvl(apartment.getRoomsArea())) * 100.0) / 100.0;
    }

    private double nvl(Double value) {
        return value == null ? 0.0 : value;
    }
}