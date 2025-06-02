package com.bookingticketservice.configuration;

import com.bookingticketservice.dto.SeatStatusUpdateDto;
import com.bookingticketservice.dto.TicketSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendSeatStatusUpdate(String scheduleId, List<String> seatScheduleIds, String status) {
        SeatStatusUpdateDto updateDto = new SeatStatusUpdateDto(scheduleId, seatScheduleIds, status);

        kafkaTemplate.send(KafkaConfig.SEAT_STATUS_UPDATES_TOPIC, scheduleId, updateDto)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent seat status update to Kafka: scheduleId={}, status={}",
                                scheduleId, status);
                    } else {
                        log.error("Failed to send seat status update to Kafka: {}", ex.getMessage());
                    }
                });
    }

    public void sendTicketNotification(TicketSummaryResponse ticketSummaryResponse) {
        String key = UUID.randomUUID().toString();

        kafkaTemplate.send(KafkaConfig.TICKET_SUMARY_TOPIC, key, ticketSummaryResponse)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Đã gửi thông báo thanh toán thành công đến Kafka: bookingId={}", key);
                    } else {
                        log.error("Không thể gửi thông báo thanh toán đến Kafka: {}", ex.getMessage());
                    }
                });
    }
}