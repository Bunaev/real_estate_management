package com.search_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "district")
public class District {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @ManyToOne
    @JoinColumn(name = "location_id")
    @JsonIgnore
    private Location location;
    @OneToMany(mappedBy = "district", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonIgnore
    private List<ResidentialComplex> residentialComplexes = new ArrayList<>();
}