package com.search_service.service;

import com.search_service.entity.Apartment;

import java.util.function.BiConsumer;

public enum ApartmentPlanType {

    PLAN("", Apartment::setPlanKey),
    ENTRANCE_PLAN("-entrance", Apartment::setEntrancePlanKey);

    private static final String FILE_EXTENSION = ".jpg";

    private final String fileNameSuffix;
    private final BiConsumer<Apartment, String> keyApplier;

    ApartmentPlanType(String fileNameSuffix, BiConsumer<Apartment, String> keyApplier) {
        this.fileNameSuffix = fileNameSuffix;
        this.keyApplier = keyApplier;
    }

    public String resolveFileName(Integer apartmentNumber) {
        return apartmentNumber + fileNameSuffix + FILE_EXTENSION;
    }

    public void applyKey(Apartment apartment, String key) {
        keyApplier.accept(apartment, key);
    }
}
