package com.prj.flashdeal.domain.order.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.entity.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 상세 응답")
public record OrderResponse(
    @Schema(description = "주문 ID", example = "1001") Long orderId,
    @Schema(description = "회원 ID", example = "42") Long memberId,
    @Schema(description = "주문 상태", example = "PENDING") OrderStatus status,
    @Schema(description = "주문 항목 목록") List<OrderItemResponse> orderItems,
    @Schema(description = "총 결제 금액", example = "50000") Integer totalPrice,
    @Schema(description = "주문 생성 일시", example = "2026-03-03T12:00:00") LocalDateTime createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getMember().getId(),
            order.getStatus(),
            order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .toList(),
            order.getTotalPrice(),
            order.getCreatedAt()
        );
    }
}
