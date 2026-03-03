package com.prj.flashdeal.domain.payment.dto.response;

import java.time.LocalDateTime;

import com.prj.flashdeal.domain.payment.entity.Payment;
import com.prj.flashdeal.domain.payment.entity.PaymentMethod;
import com.prj.flashdeal.domain.payment.entity.PaymentStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 응답")
public record PaymentResponse(
    @Schema(description = "결제 ID", example = "1")
    Long paymentId,

    @Schema(description = "주문 ID", example = "1")
    Long orderId,

    @Schema(description = "결제 상태", example = "COMPLETED")
    PaymentStatus status,

    @Schema(description = "결제 수단", example = "CARD")
    PaymentMethod method,

    @Schema(description = "결제 금액", example = "50000")
    Integer amount,

    @Schema(description = "결제 완료 시각", example = "2024-01-01T12:00:00")
    LocalDateTime paidAt,

    @Schema(description = "결제 생성 시각", example = "2024-01-01T11:59:00")
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
