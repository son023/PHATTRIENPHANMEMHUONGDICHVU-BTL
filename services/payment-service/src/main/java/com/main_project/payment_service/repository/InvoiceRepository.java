package com.main_project.payment_service.repository;

import com.main_project.payment_service.entity.Invoice;
import com.main_project.payment_service.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {
}
