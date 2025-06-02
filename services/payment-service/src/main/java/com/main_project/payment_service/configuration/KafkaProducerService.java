package com.main_project.payment_service.configuration;

import com.main_project.payment_service.dto.PaymentNotificationDto;
import com.main_project.payment_service.entity.Invoice;
import com.main_project.payment_service.entity.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentNotification(String orderCode, Invoice invoice, String status, List<String> seatScheduleIds, List<Ticket> tickets) {
        PaymentNotificationDto notification = new PaymentNotificationDto(orderCode, invoice, status, seatScheduleIds, tickets);

        kafkaTemplate.send(KafkaConfig.PAYMENT_NOTIFICATIONS_TOPIC, orderCode, notification)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent payment notification to Kafka: orderCode={}, status={}", orderCode, status);
                    } else {
                        log.error("Failed to send payment notification to Kafka: {}", ex.getMessage());
                    }
                });
    }
}