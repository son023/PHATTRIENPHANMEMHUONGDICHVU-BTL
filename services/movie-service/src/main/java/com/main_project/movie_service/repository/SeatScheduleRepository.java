package com.main_project.movie_service.repository;

import com.main_project.movie_service.entity.SeatSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatScheduleRepository extends JpaRepository<SeatSchedule, String> {

    Optional<List<SeatSchedule>> findAllByScheduleId(String scheduleId);
}
