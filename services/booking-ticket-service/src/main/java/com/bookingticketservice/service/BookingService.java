package com.bookingticketservice.service;

import com.bookingticketservice.client.MovieServiceClient;
import com.bookingticketservice.client.NotificationServiceClient;
import com.bookingticketservice.client.PaymentServiceClient;
import com.bookingticketservice.configuration.KafkaProducerService;
import com.bookingticketservice.dto.*;
import com.bookingticketservice.model.Invoice;
import com.bookingticketservice.model.Ticket;
import com.bookingticketservice.model.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService {

    PaymentServiceClient paymentServiceClient;
    MovieServiceClient movieServiceClient;
    NotificationServiceClient notificationServiceClient;

    SimpMessagingTemplate messagingTemplate;
    KafkaProducerService kafkaProducerService;


    public PaymentResponse createBooking(String token, PaymentRequest request) {
        try {
            ResponseEntity<PaymentResponse> response = paymentServiceClient.createBooking(token,request);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                PaymentResponse paymentResponse = response.getBody();
                log.info("Payment created successfully: orderCode={}", paymentResponse.getOrderCode());
                return paymentResponse;
            }
        } catch (Exception e) {
            log.error("Error creating payment: {}", e.getMessage());
            PaymentResponse errorResponse = new PaymentResponse();
            errorResponse.setStatus("ERROR");
            errorResponse.setMessage("Lỗi khi tạo thanh toán: " + e.getMessage());
            return errorResponse;
        }

        PaymentResponse errorResponse = new PaymentResponse();
        errorResponse.setStatus("ERROR");
        errorResponse.setMessage("Lỗi khi tạo thanh toán");
        return errorResponse;
    }

    public void handlePaymentStatus(String orderCode, Invoice invoice, String status,
                                    List<String> seatScheduleIds, List<Ticket> tickets) {
        User user = User.builder()
                .username(invoice.getCustomerName())
                .email(invoice.getCustomerEmail())
                .phone((invoice.getCustomerPhone()))
                .build();

        log.info("Received payment status update: orderCode={}, invoiceId={}, status={}",
                orderCode, invoice.getId(), status);
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", status);
        payload.put("invoiceId", invoice.getId());
        if (status.equals("COMPLETED")) {
            kafkaProducerService.sendSeatStatusUpdate(
                    "null",
                    seatScheduleIds,
                    "Placed"
            );

            TicketSummaryRequest ticketSummaryRequest = TicketSummaryRequest.builder()
                    .movieId(null)
                    .seatScheduleIds(seatScheduleIds)
                    .build();
            TicketSummaryResponse ticketSummaryResponse = movieServiceClient
                    .getTicketSummary(ticketSummaryRequest).getBody();
            ticketSummaryResponse.setUser(user);
            ticketSummaryResponse.setTickets(tickets);
            kafkaProducerService.sendTicketNotification(ticketSummaryResponse);
        }

        messagingTemplate.convertAndSend("/topic/payment/" + orderCode, payload);

        log.info("Updated booking status and sent WebSocket notification");

    }
}