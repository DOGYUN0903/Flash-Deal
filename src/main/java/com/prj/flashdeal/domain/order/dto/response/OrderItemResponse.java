package com.prj.flashdeal.domain.order.dto.response;

import com.prj.flashdeal.domain.order.entity.OrderItem;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 항목 응답")
public record OrderItemResponse(
    @Schema(description = "주문 항목 ID", example = "5001") Long orderItemId,
    @Schema(description = "상품 ID", example = "1") Long productId,
    @Schema(description = "상품명", example = "무선 이어폰") String productName,
    @Schema(description = "상품 단가", example = "25000") int price,
    @Schema(description = "주문 수량", example = "2") int quantity,
    @Schema(description = "항목 총 금액 (단가 × 수량)", example = "50000") int orderPrice
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
