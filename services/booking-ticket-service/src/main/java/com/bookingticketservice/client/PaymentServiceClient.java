package com.bookingticketservice.client;

import com.bookingticketservice.dto.PaymentRequest;
import com.bookingticketservice.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("payments/create")
    ResponseEntity<PaymentResponse> createBooking(
            @RequestHeader("Authorization") String token,
            @RequestBody PaymentRequest request);
}