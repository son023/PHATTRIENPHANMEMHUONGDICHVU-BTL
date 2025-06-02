package com.main_project.payment_service.service;

import com.main_project.payment_service.client.BookingTicketServiceClient;
import com.main_project.payment_service.configuration.KafkaProducerService;
import com.main_project.payment_service.dto.PaymentResponse;
import com.main_project.payment_service.dto.PaymentRequest;
import com.main_project.payment_service.entity.Invoice;
import com.main_project.payment_service.entity.Ticket;
import com.main_project.payment_service.entity.TicketInvoice;
import com.main_project.payment_service.repository.InvoiceRepository;
import com.main_project.payment_service.repository.TicketInvoiceRepository;
import com.main_project.payment_service.repository.TicketRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@EnableScheduling
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {

    PayOS payOS;
























































































































    InvoiceRepository invoiceRepository;

    TicketRepository ticketRepository;

    TicketInvoiceRepository ticketInvoiceRepository;

    BookingTicketServiceClient bookingTicketServiceClient;

    KafkaProducerService kafkaProducerService;

    private final Map<String, PaymentInfo> paymentInfoMap = new ConcurrentHashMap<>();

    private static class PaymentInfo {
        private final long orderCodeLong;
        private String status;
        private int checkCount;
        private boolean notified;
        @Setter
        @Getter
        private PaymentRequest paymentRequest;
        @Setter
        @Getter
        private List<String> seatScheduleIds;

        public PaymentInfo(long orderCodeLong) {
            this.orderCodeLong = orderCodeLong;
            this.paymentRequest = null;
            this.status = "PENDING";
            this.checkCount = 0;
            this.notified = false;
        }

        public boolean shouldRemove() {
            return (("COMPLETED".equals(status) ||
                    "FAILED".equals(status) ||
                    "CANCELED".equals(status)) && notified) ||
                    checkCount > 150;
        }
    }

    public PaymentResponse createPaymentWithBooking(PaymentRequest dto) {
        try {
            String orderCodeStr = "ORDER_" + System.currentTimeMillis();
            long orderCodeLong = Long.parseLong(String.valueOf(System.currentTimeMillis()).substring(5));

            int amount = dto.getSeatScheduleIds().size();
            String productName = "Vé xem phim";
            ItemData item = ItemData.builder()
                    .name(productName)
                    .price(amount)
                    .quantity(amount)
                    .build();

            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCodeLong)
                    .description("Vé xem phim")
                    .amount(amount*90000)
                    .returnUrl("http://localhost:3000/booking/success")
                    .cancelUrl("http://localhost:3000/booking/cancel")
                    .item(item)
                    .build();

            log.info("Calling PayOS API with data: {}", paymentData);

            CheckoutResponseData checkoutData = payOS.createPaymentLink(paymentData);

            log.info("PayOS response: {}", checkoutData);

            PaymentInfo paymentInfo = new PaymentInfo(orderCodeLong);
            paymentInfo.setSeatScheduleIds(dto.getSeatScheduleIds());
            paymentInfo.setPaymentRequest(dto);
            paymentInfoMap.put(orderCodeStr, paymentInfo);

            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setOrderCode(orderCodeStr);
            paymentResponse.setQrCode(checkoutData.getQrCode());
            paymentResponse.setPaymentLinkId(checkoutData.getPaymentLinkId());
            paymentResponse.setStatus("PENDING");
            paymentResponse.setMessage("Vui lòng thanh toán");

            return paymentResponse;
        } catch (Exception e) {
            log.error("Error creating payment: ", e);
            PaymentResponse errorResponse = new PaymentResponse();
            errorResponse.setStatus("ERROR");
            errorResponse.setMessage("Lỗi: " + e.getMessage());
            return errorResponse;
        }
    }

    private List<Ticket> createTickets(List<String> seatScheduleIds, float totalPrice, Invoice invoice) {
        float pricePerTicket = totalPrice / seatScheduleIds.size();
        List<Ticket> tickets = new ArrayList<>();
        for (String seatScheduleId : seatScheduleIds) {
            Ticket ticket = Ticket.builder()
                    .seatScheduleId(seatScheduleId)
                    .price(pricePerTicket)
                    .build();

            Ticket savedTicket = ticketRepository.save(ticket);
            tickets.add(savedTicket);
            TicketInvoice ticketInvoice = TicketInvoice.builder()
                    .invoice(invoice)
                    .ticket(savedTicket)
                    .build();

            ticketInvoiceRepository.save(ticketInvoice);
        }
        return tickets;
    }

    private String mapPayOSStatusToInternalStatus(String payosStatus) {
        switch (payosStatus) {
            case "PAID":
                return "COMPLETED";
            case "CANCELLED":
                return "CANCELED";
            case "EXPIRED":
                return "FAILED";
            default:
                return "PENDING";
        }
    }

    private void notifyBookingServiceAboutPayment(String orderCode, Invoice invoice, String status, List<String> seatScheduleIds, List<Ticket> tickets) {
        try {
            kafkaProducerService.sendPaymentNotification(orderCode, invoice, status, seatScheduleIds, tickets);
            log.info("Notified Booking Service about payment status: orderCode={}, status={}", orderCode, status);
        } catch (Exception e) {
            log.error("Failed to notify Booking Service: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 2000)
    public void checkPendingPayments() {
        if (paymentInfoMap.isEmpty()) {
            return;
        }

        log.info("Checking status of {} pending payments", paymentInfoMap.size());
        paymentInfoMap.forEach((orderCode, paymentInfo) -> {
            try {
                paymentInfo.checkCount++;
                if(paymentInfo.checkCount > 149){
                    notifyBookingServiceAboutPayment(orderCode, new Invoice(), "CANCELED", paymentInfo.getSeatScheduleIds(), new ArrayList<>());
                }
                PaymentLinkData paymentData = payOS.getPaymentLinkInformation(paymentInfo.orderCodeLong);
                log.info("Payment {} status from PayOS: {}", orderCode, paymentData.getStatus());

                String previousStatus = paymentInfo.status;
                String newStatus = mapPayOSStatusToInternalStatus(paymentData.getStatus());

                if (!previousStatus.equals(newStatus)) {
                    paymentInfo.status = newStatus;
                    Invoice invoice = saveInvoice(paymentInfo.paymentRequest);
                    List<Ticket> tickets = createTickets(paymentInfo.getSeatScheduleIds(), paymentInfo.paymentRequest.getFinalPrice(), invoice);
                    if (!paymentInfo.notified) {
                        notifyBookingServiceAboutPayment(orderCode, invoice, newStatus, paymentInfo.getSeatScheduleIds(), tickets);
                        paymentInfo.notified = true;
                    }
                }

                // for test
//                if(paymentInfo.checkCount == 2){
//                    Invoice invoice = saveInvoice(paymentInfo.paymentRequest);
//                    List<Ticket> tickets = createTickets(paymentInfo.getSeatScheduleIds(), paymentInfo.paymentRequest.getFinalPrice(), invoice);
//                    notifyBookingServiceAboutPayment(orderCode, invoice, "COMPLETED", paymentInfo.getSeatScheduleIds(), tickets);
//                }

            } catch (Exception e) {
                log.error("Error checking payment status for {}: {}", orderCode, e.getMessage());
            }
        });

        paymentInfoMap.entrySet().removeIf(entry -> entry.getValue().shouldRemove());
    }

    private Invoice saveInvoice(PaymentRequest dto) {
        //User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //String userId = currentUser.getId();
        String userId ="1";
        Invoice invoice = Invoice.builder()
                .customerName(dto.getCustomerName())
                .customerEmail(dto.getCustomerEmail())
                .customerPhone(dto.getCustomerPhone())
                .firstPrice(dto.getTotalPrice())
                .discount(dto.getDiscount())
                .lastPrice(dto.getFinalPrice())
                .status("COMPLETED")
                .timeCreated(LocalDateTime.now())
                .userId(userId)
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return savedInvoice;
    }
}