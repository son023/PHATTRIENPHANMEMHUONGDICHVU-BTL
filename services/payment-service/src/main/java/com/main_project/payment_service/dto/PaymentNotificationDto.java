package com.main_project.payment_service.dto;

import com.main_project.payment_service.entity.Invoice;
import com.main_project.payment_service.entity.Ticket;
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