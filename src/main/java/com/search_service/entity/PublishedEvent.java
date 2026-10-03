package com.search_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "published_event")
public class PublishedEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "type_event", nullable = false)
    private TaskType type;
    @Column(name = "event_content")
    private String eventContent;
    @Column(name = "start_event_date")
    private LocalDate startEventDate;
    @Column(name = "end_event_date")
    private LocalDate endEventDate;
    @Column(name = "event_picture_key")
    private String eventPictureKey;

}
