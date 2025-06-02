package com.bookingticketservice.model;

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
    private Room room;
    private Movie movie;
}
