package com.prj.flashdeal.domain.order.dto.response;

import java.time.LocalDateTime;

import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.entity.OrderStatus;

public record OrderSummaryResponse(
    Long orderId,
    OrderStatus status,
    Integer totalPrice,
    int itemCount,
    LocalDateTime createdAt
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
