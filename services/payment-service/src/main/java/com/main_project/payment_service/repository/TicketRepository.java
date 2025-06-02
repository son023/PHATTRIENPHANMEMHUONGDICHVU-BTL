package com.main_project.payment_service.repository;

import com.main_project.payment_service.entity.Ticket;
import com.main_project.payment_service.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
}
