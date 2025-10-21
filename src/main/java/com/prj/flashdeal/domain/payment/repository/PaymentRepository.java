package com.prj.flashdeal.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prj.flashdeal.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryCustom {
}
