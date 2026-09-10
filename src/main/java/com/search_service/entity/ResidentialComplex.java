package com.search_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "residential_complex")
public class ResidentialComplex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    @JsonIgnore
    private District district;
    @OneToMany(mappedBy = "residentialComplex", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonIgnore
    private List<ComplexMetroDistance> metroDistances;
    @OneToMany(mappedBy = "residentialComplex", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Building> buildings;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id")
    @JsonIgnore
    private Developer developer;
    @Column(name = "render_path")
    private String renderPath;
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;
}