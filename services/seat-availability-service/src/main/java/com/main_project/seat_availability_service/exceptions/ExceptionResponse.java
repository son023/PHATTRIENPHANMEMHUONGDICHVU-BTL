package com.main_project.seat_availability_service.exceptions;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExceptionResponse {
    int code;
    String message;
}
