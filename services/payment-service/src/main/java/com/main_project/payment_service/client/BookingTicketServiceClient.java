package com.main_project.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "booking-service")
public interface BookingTicketServiceClient {
    @PostMapping("bookings/payment-status")
    ResponseEntity<Map<String, Object>> updatePaymentStatus(
            @RequestParam("orderCode") String orderCode,
            @RequestParam("invoiceId") String invoiceId,
            @RequestParam("status") String status,
            @RequestBody List<String> seatScheduleIds
    );
}