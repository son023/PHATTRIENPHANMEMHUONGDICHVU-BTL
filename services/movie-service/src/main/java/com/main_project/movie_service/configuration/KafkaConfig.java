package com.main_project.movie_service.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String SEAT_STATUS_UPDATES_TOPIC = "seat-status-updates";

    @Bean
    public NewTopic seatStatusUpdatesTopic() {
        return TopicBuilder.name(SEAT_STATUS_UPDATES_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}