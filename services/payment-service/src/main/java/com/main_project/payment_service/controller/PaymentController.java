package com.main_project.payment_service.controller;

import com.main_project.payment_service.dto.PaymentResponse;
import com.main_project.payment_service.dto.PaymentRequest;
import com.main_project.payment_service.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPaymentWithBooking(
            @RequestBody PaymentRequest paymentBookingDTO) {
        PaymentResponse response = paymentService.createPaymentWithBooking(paymentBookingDTO);
        return ResponseEntity.ok(response);
    }

}
