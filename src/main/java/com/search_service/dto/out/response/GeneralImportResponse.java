package com.search_service.dto.out.response;

import lombok.Getter;

import java.util.List;

@Getter
public class GeneralImportResponse {
    private final int total;
    private final int success;
    private final int error;
    private final List<ApartmentImportResponse> result;

    private GeneralImportResponse(List<ApartmentImportResponse> result) {
        this.result = List.copyOf(result);
        this.total = result.size();
        this.success = (int) result.stream().filter(response -> response.getStatus() == ImportStatus.OK).count();
        this.error = total - success;
    }

    public static GeneralImportResponse of(List<ApartmentImportResponse> result) {
        return new GeneralImportResponse(result);
    }
}
