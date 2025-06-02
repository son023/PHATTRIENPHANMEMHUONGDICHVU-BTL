package com.main_project.seat_availability_service.client;

import com.main_project.seat_availability_service.model.SeatSchedule;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "movie-service")
public interface MovieServiceClient {

    @GetMapping("/movies/get-all-seat-schedules-by-schedule/{scheduleId}")
    ResponseEntity<List<SeatSchedule>> getAllSeatSchedulesBySchedule(
            @PathVariable("scheduleId") String scheduleId);
}
