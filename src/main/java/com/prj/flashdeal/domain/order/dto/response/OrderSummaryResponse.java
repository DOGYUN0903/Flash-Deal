package com.prj.flashdeal.domain.order.dto.response;

import java.time.LocalDateTime;

import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.entity.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 목록 요약 응답")
public record OrderSummaryResponse(
    @Schema(description = "주문 ID", example = "1001") Long orderId,
    @Schema(description = "주문 상태", example = "PAID") OrderStatus status,
    @Schema(description = "총 결제 금액", example = "50000") Integer totalPrice,
    @Schema(description = "주문 항목 수", example = "3") int itemCount,
    @Schema(description = "주문 생성 일시", example = "2026-03-03T12:00:00") LocalDateTime createdAt
) {
    public static OrderSummaryResponse from(Order order) {
        return new OrderSummaryResponse(
            order.getId(),
            order.getStatus(),
            order.getTotalPrice(),
            order.getOrderItems().size(),
            order.getCreatedAt()
        );
    }
}
