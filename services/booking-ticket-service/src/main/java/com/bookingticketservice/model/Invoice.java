package com.bookingticketservice.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invoice {

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
