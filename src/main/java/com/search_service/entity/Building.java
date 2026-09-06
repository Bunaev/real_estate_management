package com.search_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "building")
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "residential_complex_id")
    @JsonIgnore
    private ResidentialComplex residentialComplex;
    @OneToMany(mappedBy = "building", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonIgnore
    private List<Entrance> entrances;
    @Column(name = "completion_date")
    private LocalDate completionDate;
    @Column(name = "key_handover_date")
    private LocalDate keyHandoverDate;
}