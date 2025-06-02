package com.bookingticketservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "seat-availability-service")
public interface SeatAvalabilityServiceClient {

    @PostMapping("/seat-availability/check-selected-seats-available/{scheduleId}")
    public boolean checkSelectedSeatsAvailable(
            @RequestHeader("Authorization") String token,
            @PathVariable("scheduleId") String scheduleId,
            @RequestBody List<String> requestedSeatIds);
}
