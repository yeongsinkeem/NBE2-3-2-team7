package com.project.popupmarket.repository;

import com.project.popupmarket.entity.StagingPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface
StagingPaymentRepository extends JpaRepository<StagingPayment, String> {
}
