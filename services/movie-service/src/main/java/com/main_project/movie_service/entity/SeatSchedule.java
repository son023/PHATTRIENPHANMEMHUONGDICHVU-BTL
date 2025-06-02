package com.main_project.movie_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "seats_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    // "Empty", "Holding", "Placed"
    String status;

    LocalDateTime seatHoldStartTime;

    LocalDateTime seatHoldEndTime;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
}
