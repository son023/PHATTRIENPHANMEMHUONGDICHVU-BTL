package com.main_project.movie_service.configuration;

import com.main_project.movie_service.dto.SeatStatusUpdateDto;
import com.main_project.movie_service.dto.UpdateSeatSchedulesStatusRequest;
import com.main_project.movie_service.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final MovieService movieService;

    @KafkaListener(
            topics = KafkaConfig.SEAT_STATUS_UPDATES_TOPIC,
            groupId = "movie-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeSeatStatusUpdate(ConsumerRecord<String, Object> record) {
        log.info("Received seat status update: {}", record);

        try {
            if (record.value() instanceof SeatStatusUpdateDto) {
                SeatStatusUpdateDto update = (SeatStatusUpdateDto) record.value();
                log.info("Processing seat status update: {}", update);

                UpdateSeatSchedulesStatusRequest request =
                        new UpdateSeatSchedulesStatusRequest(update.getSeatScheduleIds(), update.getStatus());

                movieService.updateSeatSchedules(update.getScheduleId(), request);

                log.info("Successfully updated seat status");
            } else {
                log.error("Value is not SeatStatusUpdateDto: {}",
                        record.value() != null ? record.value().getClass().getName() : "null");
            }
        } catch (Exception e) {
            log.error("Error processing seat status update: {}", e.getMessage(), e);
        }
    }
}