package com.main_project.movie_service.controller;

import com.main_project.movie_service.dto.TicketSummaryRequest;
import com.main_project.movie_service.dto.TicketSummaryResponse;
import com.main_project.movie_service.dto.UpdateSeatSchedulesStatusRequest;
import com.main_project.movie_service.entity.Movie;
import com.main_project.movie_service.entity.Schedule;
import com.main_project.movie_service.entity.Seat;
import com.main_project.movie_service.entity.SeatSchedule;
import com.main_project.movie_service.service.MovieService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieController {

    @Autowired
    MovieService movieService;
//
//    @GetMapping("/test")
//    String test() {
//        return "test";
//    }

//    @GetMapping("/create-all-seat-schedules")
//    void createAllSeatSchedules() {
//        movieService.createAllSeatSchedules();
//    }

    @GetMapping("/get-all-movies")
    ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.ok().body(movieService.getAllMovies());
    }

    @GetMapping("/get-all-schedules-by-movie/{movieId}")
    ResponseEntity<List<Schedule>> getAllSchedulesByMovie(@PathVariable("movieId") String movieId) {
        return ResponseEntity.ok().body(movieService.getAllSchedulesByMovie(movieId));
    }

    @GetMapping("/get-all-seat-schedules-by-schedule/{scheduleId}")
    ResponseEntity<List<SeatSchedule>> getAllSeatSchedulesBySchedule(
            @PathVariable("scheduleId") String scheduleId) {
        return ResponseEntity.ok().body(movieService.getAllSeatSchedulesBySchedule(scheduleId));
    }

    @PostMapping("/update-seat-schedules/{scheduleId}")
    ResponseEntity<Boolean> updateSeatSchedules(
                    @PathVariable String scheduleId,
                    @RequestBody UpdateSeatSchedulesStatusRequest updateSeatSchedulesStatusRequest ) {
        return ResponseEntity.ok().body(movieService.updateSeatSchedules(scheduleId, updateSeatSchedulesStatusRequest));
    }

    @PostMapping("/get-ticket-summary")
    ResponseEntity<TicketSummaryResponse> getTicketSummary(
            @RequestBody TicketSummaryRequest ticketSummaryRequest) {
        return ResponseEntity.ok().body(movieService.getTicketSummary(ticketSummaryRequest));
    }


}
