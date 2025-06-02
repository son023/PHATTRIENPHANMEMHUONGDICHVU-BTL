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
import java.util.Map;

@FeignClient(name = "notification-service")
public interface NotificationServiceClient {

    @PostMapping("/notification/payment-success")
    ResponseEntity<Map<String, String>> sendPaymentSuccessEmail(
            @RequestBody TicketSummaryResponse ticketSummaryResponse);
}
