package com.main_project.movie_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}
