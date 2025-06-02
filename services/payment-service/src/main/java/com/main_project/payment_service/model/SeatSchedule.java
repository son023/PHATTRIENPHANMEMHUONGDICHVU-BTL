package com.main_project.payment_service.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatSchedule {
    String id;
    String status;
    LocalDateTime seatHoldStartTime;
    LocalDateTime seatHoldEndTime;
    private Seat seat;
    private Schedule schedule;
}
