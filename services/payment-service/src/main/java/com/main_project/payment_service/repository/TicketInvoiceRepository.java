package com.main_project.payment_service.repository;

import com.main_project.payment_service.entity.TicketInvoice;
import com.main_project.payment_service.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketInvoiceRepository extends JpaRepository<TicketInvoice, String> {
}
