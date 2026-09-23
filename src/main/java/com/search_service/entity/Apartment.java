package com.search_service.entity;

import com.search_service.util.cache.ExcelClass;
import com.search_service.util.cache.ExcelColumn;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "apartment")
@ExcelClass(synonyms = {"квартиры", "апартаменты", "apartments", "apart"})
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    @ExcelColumn(synonyms = {"номер", "#", "№", "ном", "number", "num"}, columnName = "№")
    private Integer number;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @ExcelColumn(synonyms = {"type", "тип", "тип квартиры", "комнаты", "количество комнат"}, columnName = "Тип")
    private ApartmentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrance_id")
    private Entrance entrance;

    @Column(name = "price")
    @ExcelColumn(synonyms = {"цена", "price", "стоимость"}, columnName = "Цена")
    private Double price;

    @Column(name = "price_per_square_meter")
    @ExcelColumn(synonyms = {"цена за м²", "цена квм", "price per m²", "PPSM"}, columnName = "Цена за м²", required = false)
    private Double pricePerSquareMeter;

    @Column(name = "floor")
    @ExcelColumn(synonyms = {"этаж", "floor"}, columnName = "Этаж")
    private Integer floor;

    @Column(name = "area")
    @ExcelColumn(synonyms = {"area", "площадь", "s"}, columnName = "Площадь", required = false)
    private Double area;

    @Column(name = "kitchen_area")
    @ExcelColumn(synonyms = {"Площадь кухни", "Кухня", "Kitchen area", "kitchen"}, columnName = "Площадь кухни")
    private Double kitchenArea;

    @Column(name = "hallway_area")
    @ExcelColumn(synonyms = {"Площадь коридора", "Коридор", "hallway area", "hallway"}, columnName = "Площадь коридора")
    private Double hallwayArea;

    @Column(name = "bathroom_area")
    @ExcelColumn(synonyms = {"Площадь санузла", "Санузел", "bathroom area", "bathroom"}, columnName = "Площадь санузла")
    private Double bathroomArea;

    @Column(name = "rooms_area")
    @ExcelColumn(synonyms = {"Площадь комнат", "Комнаты", "rooms area", "rooms"}, columnName = "Площадь комнат")
    private Double roomsArea;

    @Column(name = "has_balcony")
    @ExcelColumn(synonyms = {"наличие балкона", "балкон", "hasBalcony"}, columnName = "Балкон")
    private Boolean hasBalcony;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @ExcelColumn(synonyms = {"status", "статус"}, columnName = "Статус")
    private Status status;

    @Column(name = "bathroom_type")
    @Enumerated(EnumType.STRING)
    @ExcelColumn(synonyms = {"тип санузла", "санузел", "bathroom", "bathroomType"}, columnName = "Тип санузла")
    private BathroomType bathroomType;
    @Column(name = "plan_key")
    private String planKey;
    @Column(name = "entrance_plan_key")
    private String entrancePlanKey;
    @Column(name = "notes")
    private String notes;

}