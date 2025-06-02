package com.bookingticketservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private List<String> seatScheduleIds;
    private float totalPrice;
    private float discount;
    private float finalPrice;
}