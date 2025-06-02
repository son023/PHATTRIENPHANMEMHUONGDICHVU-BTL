package com.main_project.movie_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;

    String cover;

    String genre;

    Integer duration;

    String language;

    String director;

    String cast;

    @Size(max = 5000)
    @Column(name = "description")
    String desc;
}
