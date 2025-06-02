package com.bookingticketservice.client;

import com.bookingticketservice.dto.TicketSummaryRequest;
import com.bookingticketservice.dto.TicketSummaryResponse;
import com.bookingticketservice.dto.UpdateSeatSchedulesStatusRequest;
import com.bookingticketservice.model.Movie;
import com.bookingticketservice.model.Schedule;
import com.bookingticketservice.model.SeatSchedule;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "movie-service")
public interface MovieServiceClient {
    @GetMapping("/movies/get-all-movies")
    ResponseEntity<List<Movie>> getAllMovies(
            @RequestHeader("Authorization") String token);

    @GetMapping("/movies/get-all-schedules-by-movie/{movieId}")
    ResponseEntity<List<Schedule>> getAllSchedulesByMovie(
            @RequestHeader("Authorization") String token,
            @PathVariable("movieId") String movieId);

    @GetMapping("/movies/get-all-seat-schedules-by-schedule/{scheduleId}")
    ResponseEntity<List<SeatSchedule>> getAllSeatSchedulesBySchedule(
            @RequestHeader("Authorization") String token,
            @PathVariable("scheduleId") String scheduleId);

    @PostMapping("/movies/update-seat-schedules/{scheduleId}")
    ResponseEntity<Boolean> updateSeatSchedules(
            @RequestHeader("Authorization") String token,
            @PathVariable String scheduleId,
            @RequestBody UpdateSeatSchedulesStatusRequest updateSeatSchedulesStatusRequest );

    @PostMapping("/movies/get-ticket-summary")
    ResponseEntity<TicketSummaryResponse> getTicketSummary(
            @RequestHeader("Authorization") String token,
            @RequestBody TicketSummaryRequest ticketSummaryRequest);

    @PostMapping("/movies/get-ticket-summary")
    ResponseEntity<TicketSummaryResponse> getTicketSummary(
            @RequestBody TicketSummaryRequest ticketSummaryRequest);
}
