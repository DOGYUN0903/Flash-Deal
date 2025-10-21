package com.prj.flashdeal.domain.order.dto.response;

import com.prj.flashdeal.domain.order.entity.OrderItem;

public record OrderItemResponse(
    Long orderItemId,
    Long productId,
    String productName,
    int price,
    int quantity,
    int orderPrice
) {
    public static OrderItemResponse from(OrderItem orderItem) {
        return new OrderItemResponse(
            orderItem.getId(),
            orderItem.getProduct().getId(),
            orderItem.getProduct().getName(),
            orderItem.getPrice(),
            orderItem.getQuantity(),
            orderItem.getOrderPrice()
        );
    }
}
