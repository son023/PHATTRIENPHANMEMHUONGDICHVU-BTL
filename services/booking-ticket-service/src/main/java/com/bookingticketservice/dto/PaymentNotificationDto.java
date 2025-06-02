package com.bookingticketservice.dto;

import com.bookingticketservice.model.Invoice;
import com.bookingticketservice.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentNotificationDto {
    private String orderCode;
    private Invoice invoice;
    private String status;
    private List<String> seatScheduleIds;
    private List<Ticket> tickets;
}