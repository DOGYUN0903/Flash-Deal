package com.prj.flashdeal.domain.payment.dto.response;

import java.time.LocalDateTime;

import com.prj.flashdeal.domain.payment.entity.Payment;
import com.prj.flashdeal.domain.payment.entity.PaymentMethod;
import com.prj.flashdeal.domain.payment.entity.PaymentStatus;

public record PaymentResponse(
    Long paymentId,
    Long orderId,
    PaymentStatus status,
    PaymentMethod method,
    Integer amount,
    LocalDateTime paidAt,
    LocalDateTime createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getOrder().getId(),
            payment.getStatus(),
            payment.getMethod(),
            payment.getAmount(),
            payment.getPaidAt(),
            payment.getCreatedAt()
        );
    }
}
