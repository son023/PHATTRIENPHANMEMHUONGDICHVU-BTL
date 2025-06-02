package com.main_project.movie_service.dto;

import com.main_project.movie_service.entity.Movie;
import com.main_project.movie_service.entity.Room;
import com.main_project.movie_service.entity.Seat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketSummaryRequest {
    String movieId;
    List<String> seatScheduleIds;
}
