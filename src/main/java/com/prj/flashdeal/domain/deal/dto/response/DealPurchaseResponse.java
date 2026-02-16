package com.prj.flashdeal.domain.deal.dto.response;

import java.time.LocalDateTime;

import com.prj.flashdeal.domain.deal.entity.Deal;
import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.entity.OrderStatus;

public record DealPurchaseResponse(
    Long orderId,
    Long dealId,
    String productName,
    Integer dealPrice,
    Integer remainingStock,
    OrderStatus orderStatus,
    LocalDateTime createdAt
) {
    public static DealPurchaseResponse of(Order order, Deal deal) {
        return new DealPurchaseResponse(
            order.getId(),
            deal.getId(),
            deal.getProduct().getName(),
            deal.getDealPrice(),
            deal.getStock(),
            order.getStatus(),
            order.getCreatedAt()
        );
    }
}
