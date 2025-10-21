package com.prj.flashdeal.domain.payment.repository;

import java.util.Optional;

import com.prj.flashdeal.domain.payment.entity.Payment;

public interface PaymentRepositoryCustom {

    Optional<Payment> findByOrderId(Long orderId);
}
