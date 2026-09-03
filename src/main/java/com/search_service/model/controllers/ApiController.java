package com.search_service.model.controllers;

import com.search_service.model.entityes.*;
import com.search_service.model.entityes.dto.MetroDistanceDto;
import com.search_service.model.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {
    private final ResidentialComplexService rcService;
    private final BuildingService buildingService;
    private final EntranceService entranceService;
    private final ApartmentService apartmentService;
    private final MetroService metroService;
    private final DeveloperService developerService;
    private final DistrictService districtService;
    private final LocationService locationService;


    @GetMapping("/locations")
    public ResponseEntity<List<Location>> getLocations() {
        return ResponseEntity.ok(locationService.findAll());
    }

    @GetMapping("/districts/by-location/{locationId}")
    public List<District> getDistrictsByLocation(@PathVariable Long locationId) {
        return districtService.findByLocationId(locationId);
    }

    @GetMapping("/developers")
    public List<Developer> getDevelopers() {
        return developerService.findAll();
    }

    @GetMapping("/metro-stations")
    public List<MetroStation> getMetroStations() {
        return metroService.findAll();
    }

    @GetMapping("/complexes")
    public List<ResidentialComplex> getComplexes() {
        return rcService.findAll();
    }

    @GetMapping("/buildings/by-complex/{complexId}")
    public List<Building> getBuildingsByComplex(@PathVariable Long complexId) {
        return buildingService.findByComplexId(complexId);
    }

    @GetMapping("/entrances/by-building/{buildingId}")
    public List<Entrance> getEntrancesByBuilding(@PathVariable Long buildingId) {
        return entranceService.findByBuildingId(buildingId);
    }

    @PostMapping("/complex")
    public ResponseEntity<ResidentialComplex> createComplex(@RequestParam String name,
                                                            @RequestParam String address,
                                                            @RequestParam Long districtId,
                                                            @RequestParam Long developerId,
                                                            @RequestParam(required = false) List<Long> metroStationIds,
                                                            @RequestParam(required = false) List<Integer> metroDistances,
                                                            @RequestParam(required = false) List<String> buildingNames,
                                                            @RequestParam(required = false) List<List<String>> entranceNamesPerBuilding,
                                                            @RequestParam(required = false) List<List<Integer>> entranceMaxFloorsPerBuilding,
                                                            @RequestParam(required = false) List<String> completionDates,
                                                            @RequestParam(required = false) List<String> keyHandoverDates) {
        List<MetroDistanceDto> metroList = new ArrayList<>();
        if (metroStationIds != null && metroDistances != null && metroStationIds.size() == metroDistances.size()) {
            for (int i = 0; i < metroStationIds.size(); ++i) {
                metroList.add(MetroDistanceDto.builder().metroStationId(metroStationIds.get(i)).distance(metroDistances.get(i)).build());
            }
        }

        ResidentialComplex rc = this.rcService.create(name, address, districtId, developerId, metroList);
        if (buildingNames != null && entranceNamesPerBuilding != null) {
            for (int b = 0; b < buildingNames.size(); ++b) {
                LocalDate completion = null;
                LocalDate handover = null;
                if (completionDates != null && b < completionDates.size() && completionDates.get(b) != null && !completionDates.get(b).isEmpty()) {
                    completion = LocalDate.parse(completionDates.get(b));
                }

                if (keyHandoverDates != null && b < keyHandoverDates.size() && keyHandoverDates.get(b) != null && !keyHandoverDates.get(b).isEmpty()) {
                    handover = LocalDate.parse(keyHandoverDates.get(b));
                }

                Building building = buildingService.create(buildingNames.get(b), rc.getId(), completion, handover);
                List<String> entrances = entranceNamesPerBuilding.get(b);
                List<Integer> floors = entranceMaxFloorsPerBuilding.get(b);
                if (entrances != null && floors != null) {
                    for (int e = 0; e < entrances.size(); ++e) {
                        entranceService.create(entrances.get(e), building.getId(), floors.get(e));
                    }
                }
            }
        }

        return ResponseEntity.ok(rc);
    }

    @PostMapping("/building")
    public ResponseEntity<Building> createBuilding(@RequestParam String name, @RequestParam Long residentialComplexId, @RequestParam(required = false) String completionDate, @RequestParam(required = false) String keyHandoverDate, @RequestParam(required = false) List<String> entranceNames, @RequestParam(required = false) List<Integer> entranceMaxFloors) {
        Building building = buildingService.create(name, residentialComplexId, completionDate == null ? null : LocalDate.parse(completionDate), keyHandoverDate == null ? null : LocalDate.parse(keyHandoverDate));
        if (entranceNames != null && entranceMaxFloors != null && entranceNames.size() == entranceMaxFloors.size()) {
            for (int i = 0; i < entranceNames.size(); ++i) {
                entranceService.create(entranceNames.get(i), building.getId(), entranceMaxFloors.get(i));
            }
        }

        return ResponseEntity.ok(building);
    }

    @PostMapping("/entrance")
    public ResponseEntity<Entrance> createEntrance(@RequestParam String name, @RequestParam Long buildingId, @RequestParam Integer maxFloors) {
        return ResponseEntity.ok(entranceService.create(name, buildingId, maxFloors));
    }

    @PostMapping(value = "/import-apartments", consumes = "multipart/form-data")
    public ResponseEntity<String> importApartments(@RequestParam("entranceId") Long entranceId, @RequestPart("file") MultipartFile file) {
        try {
            List<Apartment> apartments = apartmentService.importApartments(entranceId, file);
            return ResponseEntity.ok("Загружено " + apartments.size() + " квартир");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/apartments/{entranceId}")
    public ResponseEntity<byte[]> exportApartments(@PathVariable Long entranceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "apartments.xlsx");
        return ResponseEntity.ok().headers(headers).body(apartmentService.exportApartmentByEntranceId(entranceId));
    }

    @PostMapping("/location")
    public ResponseEntity<Location> createLocation(@RequestParam String name) {
        return ResponseEntity.ok(locationService.create(name));
    }

    @PostMapping("/district")
    public ResponseEntity<District> createDistrict(@RequestParam String name, @RequestParam Long locationId) {
        return ResponseEntity.ok(districtService.create(name, locationId));
    }

    @PostMapping("/developer")
    public ResponseEntity<Developer> createDeveloper(@RequestParam String name) {
        return ResponseEntity.ok(developerService.create(name));
    }

    @DeleteMapping("/complex/{id}")
    public ResponseEntity<Void> deleteComplex(@PathVariable Long id) {
        this.rcService.delete(id);
        return ResponseEntity.ok().build();
    }
}
