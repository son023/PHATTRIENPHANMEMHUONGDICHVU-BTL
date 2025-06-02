package com.main_project.movie_service.repository;

import com.main_project.movie_service.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, String> {

    Optional<List<Seat>> findAllByRoomId(String roomId);
}
