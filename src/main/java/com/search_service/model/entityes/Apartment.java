package com.search_service.model.entityes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.search_service.model.utils.cache.ExcelClass;
import com.search_service.model.utils.cache.ExcelColumn;

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
    @Column(name = "floor")
    @ExcelColumn(synonyms = {"этаж", "floor"}, columnName = "Этаж")
    private Integer floor;
    @Column(name = "area")
    @ExcelColumn(synonyms = {"area", "площадь", "s"}, columnName = "Площадь")
    private Double area;
    @Column(name = "has_balcony")
    @ExcelColumn(synonyms = {"наличие балкона", "балкон", "has balcony"}, columnName = "Балкон")
    private Boolean hasBalcony;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @ExcelColumn(synonyms = {"status", "статус"}, columnName = "Статус")
    private Status status;
    @Column(name = "bathroom_type")
    @Enumerated(EnumType.STRING)
    @ExcelColumn(synonyms = {"тип санузла", "санузел", "bathroom", "bathroom type"}, columnName = "Тип санузла")
    private BathroomType bathroomType;

}

