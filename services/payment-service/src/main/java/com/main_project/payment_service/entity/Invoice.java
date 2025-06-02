package com.main_project.payment_service.entity;

import com.main_project.payment_service.model.SeatSchedule;
import com.main_project.payment_service.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String customerName;
    String customerEmail;
    String customerPhone;

    float discount;
    float firstPrice;
    float lastPrice;

    String status;
    LocalDateTime timeCreated;

    String userId;
}
