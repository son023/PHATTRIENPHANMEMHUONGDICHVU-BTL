package com.bookingticketservice.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String PAYMENT_NOTIFICATIONS_TOPIC = "payment-notifications";
    public static final String SEAT_STATUS_UPDATES_TOPIC = "seat-status-updates";
    public static final String TICKET_SUMARY_TOPIC = "ticket-sumary";

    @Bean
    public NewTopic seatStatusUpdatesTopic() {
        return TopicBuilder.name(SEAT_STATUS_UPDATES_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic paymentNotificationsTopic() {
        return TopicBuilder.name(PAYMENT_NOTIFICATIONS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ticketSumaryTopic() {
        return TopicBuilder.name(TICKET_SUMARY_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}