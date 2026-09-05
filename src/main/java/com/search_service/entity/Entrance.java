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
@Table(name = "entrance")
public class Entrance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "entrance", cascade = {CascadeType.ALL})
    @JsonIgnore
    private List<Apartment> apartments = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "building_id")
    @JsonIgnore
    private Building building;
    @Column(name = "max_floors")
    private Integer maxFloors;
}