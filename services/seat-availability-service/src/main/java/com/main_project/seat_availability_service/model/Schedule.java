package com.main_project.seat_availability_service.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Schedule {
    String id;
    LocalDateTime start;
    LocalDateTime end;
    String type;
    private Room room;
    private Movie movie;
}
