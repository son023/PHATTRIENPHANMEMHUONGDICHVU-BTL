package com.bookingticketservice.dto;

import com.bookingticketservice.model.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketSummaryResponse {
    User user = null;
    Movie movie;
    Room room;
    Schedule schedule;
    List<Seat> seats;
    List<Ticket> tickets = null;
    float totalPrice;
}
