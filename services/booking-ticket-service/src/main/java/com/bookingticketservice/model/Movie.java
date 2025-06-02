package com.bookingticketservice.model;

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
    Integer duration;
    String language;
    String director;
    String cast;
    String desc;
}
