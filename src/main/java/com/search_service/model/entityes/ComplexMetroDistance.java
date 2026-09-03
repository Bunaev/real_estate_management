package com.search_service.model.entityes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "complex_metro_distance")
public class ComplexMetroDistance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "residential_complex_id", nullable = false)
    private ResidentialComplex residentialComplex;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metro_station_id", nullable = false)
    private MetroStation metroStation;
    @Column(name = "distance")
    private Integer distance;
}

