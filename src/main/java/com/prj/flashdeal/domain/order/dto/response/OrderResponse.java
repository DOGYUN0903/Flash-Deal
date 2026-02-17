package com.prj.flashdeal.domain.order.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.entity.OrderStatus;

public record OrderResponse(
    Long orderId,
    Long memberId,
    OrderStatus status,
    List<OrderItemResponse> orderItems,
    Integer totalPrice,
    LocalDateTime createdAt
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
