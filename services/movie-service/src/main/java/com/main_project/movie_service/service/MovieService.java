package com.main_project.movie_service.service;

import com.main_project.movie_service.dto.TicketSummaryRequest;
import com.main_project.movie_service.dto.TicketSummaryResponse;
import com.main_project.movie_service.dto.UpdateSeatSchedulesStatusRequest;
import com.main_project.movie_service.entity.*;
import com.main_project.movie_service.repository.MovieRepository;
import com.main_project.movie_service.repository.ScheduleRepository;
import com.main_project.movie_service.repository.SeatScheduleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieService {

    @Autowired
    MovieRepository movieRepository;
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    SeatScheduleRepository seatScheduleRepository;

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public List<Schedule> getAllSchedulesByMovie(String movieId) {
        return scheduleRepository.findAllByMovieId(movieId).get();
    }

    public List<SeatSchedule> getAllSeatSchedulesBySchedule(String scheduleId) {
        List<SeatSchedule> seatSchedules = seatScheduleRepository
                .findAllByScheduleId(scheduleId).get();

        List<SeatSchedule> updatedSeatSchedules = seatSchedules.stream()
                .map(seatSchedule -> {
                    if (seatSchedule.getStatus().equals("Holding") &&
                            seatSchedule.getSeatHoldEndTime().isBefore(LocalDateTime.now())) {
                        seatSchedule.setStatus("Empty");
                    }
                    return seatSchedule;
                })
                .collect(Collectors.toList());

        seatScheduleRepository.saveAll(updatedSeatSchedules);
        return updatedSeatSchedules;
    }

    public Boolean updateSeatSchedules(String scheduleId,
                                       UpdateSeatSchedulesStatusRequest updateSeatSchedulesStatusRequest) {
        List<SeatSchedule> seatSchedules;
        if (scheduleId.equals("null")) {
            seatSchedules = seatScheduleRepository.findAll();
        } else {
            seatSchedules = seatScheduleRepository
                    .findAllByScheduleId(scheduleId).get();
        }
        if (seatSchedules == null || seatSchedules.isEmpty()) {
            return false;
        }

        List<SeatSchedule> updateSeatSchedules = seatSchedules
                .stream().filter(seatScheduleId -> updateSeatSchedulesStatusRequest
                        .getSeatScheduleIds().contains(seatScheduleId.getId()))
                .collect(Collectors.toList());
        if (updateSeatSchedules.size() !=
                updateSeatSchedulesStatusRequest.getSeatScheduleIds().size()) {
            return false;
        }

        AtomicBoolean check = new AtomicBoolean(true);
        String stateChange = updateSeatSchedulesStatusRequest.getTargetStatus();
        updateSeatSchedules = updateSeatSchedules
                .stream().map(seatSchedule -> {
                    if (stateChange.equals("Holding")) {
                        if(seatSchedule.getStatus().equals("Empty")) {
                            seatSchedule.setSeatHoldStartTime(LocalDateTime.now());
                            seatSchedule.setSeatHoldEndTime(LocalDateTime.now().plusMinutes(5));
                        }
                        else{
                            check.set(false);
                        }

                    }
                    seatSchedule.setStatus(stateChange);

                    return seatSchedule;
                })
                .collect(Collectors.toList());
        if(!check.get()) return false;
        seatScheduleRepository.saveAll(updateSeatSchedules);
        return true;
    }

    public List<Seat> getSeatsBySeatSchedules(List<String> seatScheduleIds) {
        List<SeatSchedule> seatSchedules = seatScheduleRepository
                .findAllById(seatScheduleIds);

        List<Seat> seats = seatSchedules.stream()
                .map(seatSchedule -> seatSchedule.getSeat())
                .collect(Collectors.toList());
        return seats;
    }

    public TicketSummaryResponse getTicketSummary(TicketSummaryRequest ticketSummaryRequest) {
        List<SeatSchedule> seatSchedules = seatScheduleRepository
                .findAllById(ticketSummaryRequest.getSeatScheduleIds());
        List<Seat> seats = seatSchedules.stream()
                .map(seatSchedule -> seatSchedule.getSeat())
                .collect(Collectors.toList());
        Room room = seats.get(0).getRoom();
        Schedule schedule = seatSchedules.get(0).getSchedule();

        Movie movie;
        if (ticketSummaryRequest.getMovieId() == null) {
            movie = movieRepository.findById(schedule.getMovie().getId()).get();
        } else {
            movie = movieRepository.findById(ticketSummaryRequest.getMovieId()).get();
        }

        return TicketSummaryResponse.builder()
                .movie(movie)
                .room(room)
                .schedule(schedule)
                .seats(seats)
                .totalPrice(90000 * seats.size())
                .build();
    }


//    public void createAllSeatSchedules() {
//        List<Schedule> schedules = scheduleRepository.findAll();
//
//        for(Schedule schedule: schedules) {
//            List<Seat> seats = getAllSeatsBySchedule(schedule.getId());
//            for(Seat seat: seats) {
//                SeatSchedule newSeatSchedule = SeatSchedule.builder()
//                        .status("Empty")
//                        .seat(seat)
//                        .schedule(schedule)
//                        .build();
//                seatScheduleRepository.save(newSeatSchedule);
//            }
//        }
//    }
}
