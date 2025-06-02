package com.bookingticketservice.configuration;

import com.bookingticketservice.dto.PaymentNotificationDto;
import com.bookingticketservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final BookingService bookingService;

    @KafkaListener(
            topics = "payment-notifications",
            groupId = "booking-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumePaymentNotification(ConsumerRecord<String, Object> record) {
        log.info("Received raw record: {}", record);

        try {
            if (record.value() instanceof PaymentNotificationDto) {
                PaymentNotificationDto notification = (PaymentNotificationDto) record.value();
                log.info("Successfully extracted notification: {}", notification);

                bookingService.handlePaymentStatus(
                        notification.getOrderCode(),
                        notification.getInvoice(),
                        notification.getStatus(),
                        notification.getSeatScheduleIds(),
                        notification.getTickets());
            } else {
                log.error("Value is not PaymentNotificationDto: {}",
                        record.value() != null ? record.value().getClass().getName() : "null");
            }
        } catch (Exception e) {
            log.error("Error processing record: {}", e.getMessage(), e);
            throw e;
        }
    }
}