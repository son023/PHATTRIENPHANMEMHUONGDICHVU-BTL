package com.bookingticketservice.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateSeatSchedulesStatusRequest {
    List<String> seatScheduleIds;
    String targetStatus;
}
