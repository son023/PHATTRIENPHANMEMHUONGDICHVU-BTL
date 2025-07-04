package com.main_project.movie_service.exceptions.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNCATEGORIED(1000, "Uncategoried error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1002, "User not existed", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(1003, "Invalid credentials", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1004, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZATION(1005, "Unauthorization", HttpStatus.FORBIDDEN),

    SCHEDULE_NOT_EXISTED(1006, "Schedule not existed", HttpStatus.BAD_REQUEST),
    SEAT_NOT_EXISTED(1007, "Seat not existed", HttpStatus.BAD_REQUEST),
    SEAT_SCHEDULE_NOT_EXISTED(1008, "Seat schedule not existed", HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;
}
