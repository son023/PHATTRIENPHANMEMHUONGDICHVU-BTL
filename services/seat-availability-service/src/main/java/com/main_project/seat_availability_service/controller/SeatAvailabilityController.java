package com.main_project.seat_availability_service.controller;

import com.main_project.seat_availability_service.client.MovieServiceClient;
import com.main_project.seat_availability_service.model.SeatSchedule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/seat-availability")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeatAvailabilityController {
    MovieServiceClient movieServiceClient;

    @PostMapping("/check-selected-seats-available/{scheduleId}")
    public boolean checkSelectedSeatsAvailable(
            @PathVariable("scheduleId") String scheduleId,
            @RequestBody List<String> requestedSeatIds) {

        List<SeatSchedule> allSeatSchedules = movieServiceClient
                .getAllSeatSchedulesBySchedule(scheduleId).getBody();
        if (allSeatSchedules == null || allSeatSchedules.isEmpty()) {
            return false;
        }

        List<SeatSchedule> matchedSeatSchedules = allSeatSchedules.stream()
                .filter(seatSchedule -> requestedSeatIds.contains(seatSchedule.getId()))
                .collect(Collectors.toList());
        if (matchedSeatSchedules.size() != requestedSeatIds.size()) {
            return false;
        }

        return matchedSeatSchedules.stream()
                .allMatch(seatSchedule -> "Empty".equals(seatSchedule.getStatus()));
    }
}
