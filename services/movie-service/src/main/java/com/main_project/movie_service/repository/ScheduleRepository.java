package com.main_project.movie_service.repository;

import com.main_project.movie_service.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, String> {

    Optional<List<Schedule>> findAllByMovieId(String movieId);
}
