package com.prj.flashdeal.domain.cart.dto.response;

import com.prj.flashdeal.domain.cart.entity.CartItem;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "장바구니 항목 응답")
public record CartItemResponse(
    @Schema(description = "장바구니 항목 ID", example = "10") Long cartItemId,
    @Schema(description = "상품 ID", example = "1") Long productId,
    @Schema(description = "상품명", example = "플래시 딜 상품 A") String productName,
    @Schema(description = "상품 단가 (원)", example = "15000") int price,
    @Schema(description = "수량", example = "2") int quantity,
    @Schema(description = "항목 합계 금액 (단가 × 수량, 원)", example = "30000") int totalPrice
) {
    public static CartItemResponse from(CartItem cartItem) {
        return new CartItemResponse(
            cartItem.getId(),
            cartItem.getProduct().getId(),
            cartItem.getProduct().getName(),
            cartItem.getProduct().getPrice(),
            cartItem.getQuantity(),
            cartItem.calculateItemTotalPrice()
        );
    }
}
