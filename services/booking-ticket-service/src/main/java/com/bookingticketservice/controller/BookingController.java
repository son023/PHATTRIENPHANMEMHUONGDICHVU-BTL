package com.bookingticketservice.controller;

import com.bookingticketservice.client.MovieServiceClient;
import com.bookingticketservice.client.SeatAvalabilityServiceClient;
import com.bookingticketservice.dto.*;
import com.bookingticketservice.model.Movie;
import com.bookingticketservice.model.Schedule;
import com.bookingticketservice.model.SeatSchedule;
import com.bookingticketservice.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@Slf4j
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    MovieServiceClient movieServiceClient;
    @Autowired
    SeatAvalabilityServiceClient seatAvalabilityServiceClient;

    @GetMapping("/get-all-movies")
    ResponseEntity<List<Movie>> getAllMovies(
            @RequestHeader("Authorization") String token) {
        return movieServiceClient.getAllMovies(token);
    };

    @GetMapping("/get-all-schedules-by-movie/{movieId}")
    ResponseEntity<List<Schedule>> getAllSchedulesByMovie(
            @RequestHeader("Authorization") String token,
            @PathVariable("movieId") String movieId) {
        return movieServiceClient.getAllSchedulesByMovie(token, movieId);
    };

    @GetMapping("/get-all-seat-schedules-by-schedule/{scheduleId}")
    ResponseEntity<List<SeatSchedule>> getAllSeatSchedulesBySchedule(
            @RequestHeader("Authorization") String token,
            @PathVariable("scheduleId") String scheduleId) {
        return movieServiceClient.getAllSeatSchedulesBySchedule(token, scheduleId);
    };

    @PostMapping("/update-seat-schedules/{scheduleId}")
    ResponseEntity<Boolean> updateSeatSchedules(
            @RequestHeader("Authorization") String token,
            @PathVariable String scheduleId,
            @RequestBody UpdateSeatSchedulesStatusRequest updateSeatSchedulesStatusRequest ) {
        return movieServiceClient.updateSeatSchedules(
                token, scheduleId, updateSeatSchedulesStatusRequest);
    };

    @PostMapping("/check-selected-seats-available/{scheduleId}")
    public boolean checkSelectedSeatsAvailable(
            @RequestHeader("Authorization") String token,
            @PathVariable("scheduleId") String scheduleId,
            @RequestBody List<String> requestedSeatIds) {
        return seatAvalabilityServiceClient.checkSelectedSeatsAvailable(
                token, scheduleId, requestedSeatIds);
    };

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createBooking(
            @RequestHeader("Authorization") String token,
            @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(token, request));
    }

    @PostMapping("/get-ticket-summary")
    ResponseEntity<TicketSummaryResponse> getTicketSummary(
            @RequestHeader("Authorization") String token,
            @RequestBody TicketSummaryRequest ticketSummaryRequest) {
        return movieServiceClient.getTicketSummary(token, ticketSummaryRequest);
    };
}