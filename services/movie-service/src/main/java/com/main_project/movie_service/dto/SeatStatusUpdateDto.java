package com.main_project.movie_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatStatusUpdateDto {
    private String scheduleId;
    private List<String> seatScheduleIds;
    private String status;
}