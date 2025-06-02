package com.main_project.payment_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Movie {
    String id;
    String name;
    String cover;
    String genre;
    int duration;
    String language;
    String director;
    String cast;
    String desc;
}
